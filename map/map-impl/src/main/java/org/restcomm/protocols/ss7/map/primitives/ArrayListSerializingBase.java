
package org.restcomm.protocols.ss7.map.primitives;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("arrayListSerializingBase")
public class ArrayListSerializingBase<T> {

    private ArrayList<T> data = new ArrayList<T>();
    private String elementName;
    private Class<? extends T> classDef;

    public ArrayListSerializingBase(String elementName, Class<? extends T> classDef, ArrayList<T> data) {
        this.data = data;
        this.classDef = classDef;
        this.elementName = elementName;
    }

    public ArrayListSerializingBase(String elementName, Class<? extends T> classDef) {
        this.elementName = elementName;
        this.classDef = classDef;
    }

    public ArrayList<T> getData() {
        return this.data;
    }

}
