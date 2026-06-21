package org.restcomm.protocols.ss7.map.load.ussd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configurable USSD menu tree matching {@code ussdgateway/tools/grpc-as-tester/menu_config.json}.
 * Tracks per-dialog menu state and picks the next user digit for load-test profiles.
 */
public class UssdMenuEngine {

    public enum Profile {
        RANDOM,
        BALANCE,
        DATA,
        SUBSCRIBE,
        ADAPTIVE
    }

    private final String root;
    private final Map<String, MenuNode> nodes;
    private final Map<Long, String> dialogNodes = new ConcurrentHashMap<Long, String>();
    private final Map<Long, Integer> scriptIndexes = new ConcurrentHashMap<Long, Integer>();
    private final Random random = new Random();

    public UssdMenuEngine(String configResource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = UssdMenuEngine.class.getResourceAsStream(configResource);
        if (in == null) {
            throw new IOException("Menu config not found on classpath: " + configResource);
        }
        JsonNode cfg = mapper.readTree(in);
        this.root = cfg.get("root").asText();
        this.nodes = new LinkedHashMap<String, MenuNode>();
        JsonNode nodeMap = cfg.get("nodes");
        for (String name : iterable(nodeMap.fieldNames())) {
            JsonNode n = nodeMap.get(name);
            Map<String, String> options = new LinkedHashMap<String, String>();
            if (n.has("options")) {
                JsonNode opts = n.get("options");
                for (String key : iterable(opts.fieldNames())) {
                    options.put(key, opts.get(key).asText());
                }
            }
            nodes.put(name, new MenuNode(name, n.path("text").asText(), options, n.path("final").asBoolean(false)));
        }
    }

    public static UssdMenuEngine defaultEngine() throws IOException {
        return new UssdMenuEngine("/menu_config.json");
    }

    /** Subscriber starts at root after MO Begin (AS shows main menu). */
    public void beginDialog(long dialogId) {
        dialogNodes.put(dialogId, root);
        scriptIndexes.put(dialogId, 0);
    }

    /**
     * Pick the next USSD digit(s) to send for the current dialog node and profile.
     * Advances local menu state to mirror the gRPC {@code MenuEngine}.
     */
    public String nextInput(long dialogId, Profile profile) {
        String nodeName = dialogNodes.getOrDefault(dialogId, root);
        MenuNode node = nodes.get(nodeName);
        if (node == null || node.finalNode || node.options.isEmpty()) {
            return null;
        }
        String choice = pickChoice(node, profile, dialogId);
        if (choice == null) {
            return null;
        }
        String next = node.options.get(choice);
        if (next == null) {
            next = node.options.get("*");
        }
        if ("__end__".equals(next)) {
            dialogNodes.remove(dialogId);
        } else if (next != null) {
            MenuNode nxt = nodes.get(next);
            if (nxt != null && nxt.finalNode) {
                dialogNodes.remove(dialogId);
            } else {
                dialogNodes.put(dialogId, next);
            }
        }
        return choice;
    }

    public void clearDialog(long dialogId) {
        dialogNodes.remove(dialogId);
        scriptIndexes.remove(dialogId);
    }

    private static String[] scriptFor(Profile profile) {
        switch (profile) {
            case BALANCE:
                return new String[] { "1", "0" };
            case DATA:
                return new String[] { "2", "1" };
            case SUBSCRIBE:
                return new String[] { "3", "100" };
            default:
                return null;
        }
    }

    private String pickChoice(MenuNode node, Profile profile, long dialogId) {
        String[] script = scriptFor(profile);
        if (script != null) {
            int idx = scriptIndexes.getOrDefault(dialogId, 0);
            if (idx < script.length) {
                String digit = script[idx];
                scriptIndexes.put(dialogId, idx + 1);
                if (node.options.containsKey(digit) || node.options.containsKey("*")) {
                    return digit;
                }
            }
        }
        return randomChoice(node);
    }

    private String randomChoice(MenuNode node) {
        List<String> keys = new ArrayList<String>(node.options.keySet());
        if (keys.isEmpty()) {
            return null;
        }
        Collections.shuffle(keys, random);
        for (String k : keys) {
            if (!"*".equals(k)) {
                return k;
            }
        }
        return keys.get(0);
    }

    private static Iterable<String> iterable(final java.util.Iterator<String> it) {
        return new Iterable<String>() {
            public java.util.Iterator<String> iterator() {
                return it;
            }
        };
    }

    private static final class MenuNode {
        final String name;
        final String text;
        final Map<String, String> options;
        final boolean finalNode;

        MenuNode(String name, String text, Map<String, String> options, boolean finalNode) {
            this.name = name;
            this.text = text;
            this.options = options;
            this.finalNode = finalNode;
        }
    }
}
