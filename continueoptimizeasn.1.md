Tiếp tục optimize asn.1 encode/decode ber/der với toed giống OSS Nokalva

Tuy nhiên, để ép hiệu năng lên mức "nghiệt ngã" (Ultra-Optimization) nhằm chạm mốc 1 triệu TPS ổn định, mã nguồn hiện tại vẫn còn một vài điểm nghẽn có thể tối ưu sâu hơn nữa:1. Triệt tiêu rác sinh ra từ AsnReaderHelper.readOctetStringViewTrong file AsnReaderHelper.java, hàm đọc chuỗi byte đang được viết như sau:javapublic static OctetStringView readOctetStringView(AsnMessageIndex index, int tagIndex) {
    return new OctetStringView(index.rawBuffer, index.valueOffsets[tagIndex], index.valueLengths[tagIndex]);
}
Use code with caution.Điểm nghẽn: Việc tạo mới OctetStringView hàng triệu lần/giây sinh rác trên Heap, làm giảm hiệu quả kiến trúc Flat ASN.Giải pháp: Bỏ OctetStringView, trả về trực tiếp offset/length hoặc truyền đối tượng đích vào để ghi đè dữ liệu trực tiếp.2. Tối ưu hóa bộ nhớ đệm Cache-Line cho AsnMessageIndexCấu trúc mảng hiện tại đang sử dụng 3 mảng int[] độc lập.Điểm nghẽn: Quét tuyến tính qua 3 mảng gây ra hiện tượng Cache Miss liên tục khi chạy đa luồng.Giải pháp (Interleaved Array): Gộp tags, offsets, lengths vào một mảng duy nhất theo cấu trúc [tag, off, len, parent]. Bước nhảy vòng lặp sẽ là i += ENTRY_SIZE, tận dụng tối đa cơ chế nạp trước của CPU Cache.3. Tối ưu hóa thuật toán tìm kiếm (findTagIndex)Điểm nghẽn: Vòng lặp for tuần tự trên mảng có độ phức tạp \(O(N \times M)\).Giải pháp: Sử dụng Direct-mapped Array (mảng băm phẳng) với kích thước MAX_TAGS (256). Khi parsing, gán thẳng index.tagMap[tag & 0xFF] = tagIndex; để đạt độ phức tạp \(O(1)\) khi tìm kiếm.4. Tối ưu hóa AsnInputStream trong vòng lặp NettyĐiểm nghẽn: Hàm viewBytes vẫn khởi tạo new AsnInputStream(buffer) mới cho mỗi bản tin.Giải pháp: Sử dụng ThreadLocal để tái sử dụng AsnInputStream hiện có và chỉ gọi hàm reset(buffer) hoặc setSlice() để cập nhật dữ liệu, loại bỏ hoàn toàn việc cấp phát Object.Kết luận: Áp dụng kết hợp Interleaved Array và loại bỏ OctetStringView sẽ giúp tầng parsing ASN chạy mượt mà, gần như không tạo rác (ZGC Allocation Rate).


Mình đã đọc diff. Đánh giá tổng thể:

Đây là một bước đi rất đúng hướng cho mục tiêu telecom-grade throughput.

Bạn đã loại bỏ được 2 thứ đắt nhất trong ASN.1 runtime:

ASN object tree
byte[] copy không cần thiết

Nhưng nếu mục tiêu thực sự là:

1,000,000 TPS
TCAP/MAP/CAP
32 core

thì commit này mới chỉ đạt khoảng 30-40% quãng đường.

Điểm mạnh nhất của commit
1. Flat index thay ASN tree
int[] tags
int[] valueOffsets
int[] valueLengths
int[] parents

Đây là hướng giống:

OSS Nokalva
Objective Systems ASN1C
Erlang ASN1 compiler

Thay vì:

TLV
 ├─ TLV
 ├─ TLV
 └─ TLV

Bạn đang có:

Tag#0
Tag#1
Tag#2
Tag#3

CPU cache friendly hơn rất nhiều.

2. Zero-copy OCTET STRING
OctetStringView

rất tốt.

Thay vì:

new byte[len]

mỗi lần decode.

3. ThreadLocal pool
ThreadLocal<AsnMessageIndex>

cũng đúng hướng.

TPS cao thì:

new AsnMessageIndex()

là kẻ giết GC.

Chỗ mình thấy còn rất đắt
Vấn đề #1

FlatAsnParser mỗi lần parse vẫn allocate

int[] parentStack = new int[MAX_DEPTH];

trong:

parseAll()

1 triệu TPS nghĩa là:

1,000,000 int[16]

mỗi giây.

Nên chuyển thành:

class AsnMessageIndex {

    final int[] parentStack = new int[32];

}

rồi reuse.

Riêng chỗ này có thể giảm vài GB allocation mỗi phút.

Vấn đề #2

MAX_DEPTH = 16

private static final int MAX_DEPTH = 16;

MAP ExtensionContainer có thể vượt.

CAP dialogue đôi khi khá sâu.

Nếu overflow:

parentStack[stackPointer++]

sẽ crash.

Nên:

32

hoặc

64
Vấn đề #3

Cực kỳ quan trọng

findTagIndex()

Hiện tại:

for (int i = 0; i < index.tagCount; i++)

Độ phức tạp:

O(n)

Nếu MAP message có:

100 tags

và POJO decode gọi:

findTagIndex()

20 lần

thì:

2000 comparisons

cho một message.

Đây sẽ là bottleneck số 1.

OSS Nokalva không làm vậy.

Họ build:

tag -> index

trong lúc parse.

Ví dụ:

int[] firstOccurrence = new int[256];

Parse:

firstOccurrence[tag] = idx;

Lookup:

O(1)

TPS tăng rất rõ.

Vấn đề #4

Nghiêm trọng hơn

findNthChildTag()

Hiện tại:

for (int i = 0; i < index.tagCount; i++)

Mỗi child lookup:

O(n)

Nếu decode MAP ATI:

root
 ├ subscriberIdentity
 ├ requestedInfo
 ├ gsmSCFAddress

Bạn sẽ scan toàn bộ index nhiều lần.

Nên build:

firstChild[]
nextSibling[]

giống AST.

Ví dụ:

parent
  |
firstChild
  |
nextSibling

Lookup child:

O(children)

thay vì

O(all tags)
Vấn đề #5

Tag lưu sai cho telecom

Bạn đang lưu:

index.tags[currentTagIndex] = firstTagByte;

Điều này chỉ đúng với:

tag < 31

Nhưng ASN.1 hỗ trợ:

multibyte tag

Bạn đã parse:

tagNum

nhưng không lưu.

Nên:

index.tagNumbers[]
index.tagClasses[]

Để tránh:

getTagClass()
getTagNumber()

lặp lại.

Vấn đề #6

Branch prediction

Trong hot loop:

if (indefinite)

Telecom BER gần như:

99.99%
definite length

Nên đổi:

if (!indefinite) {
   ...
} else {
   ...
}

JIT branch prediction tốt hơn.

Vấn đề #7

readLength()

Hiện tại:

cursor += lengthFieldSize(...)

Bạn đang parse length 2 lần.

Lần 1:

readLength()

Lần 2:

lengthFieldSize()

OSS Nokalva thường trả:

length
bytesConsumed

cùng lúc.

Ví dụ:

long packed
length = packed >>> 32
size   = packed & 0xffffffff

Giảm khoảng 10-15% CPU cho parser.

Vấn đề #8

MAX_TAGS = 256

public static final int MAX_TAGS = 256;

Với CAP dialogue lớn:

CAMEL
ExtensionContainer
PrivateExtension

256 không phải luôn đủ.

Nếu overflow:

return;

Đây là bug.

Message bị truncate.

Nên:

throw AsnException

hoặc

dynamic grow
Vấn đề #9

AsnReaderHelper

readInteger()

Hiện tại:

for (...)

Nếu:

INTEGER len=1
INTEGER len=2
INTEGER len=4

chiếm 99%.

Tạo fast path:

switch(len)

JIT sẽ inline.

Kiến trúc tiếp theo nếu muốn 1M TPS

Hiện tại:

BER
 ↓
FlatIndex
 ↓
ReaderHelper
 ↓
POJO

Mình sẽ đi tiếp:

BER
 ↓
FlatIndex
 ↓
Generated MAP Decoder
 ↓
POJO

Tức là codegen:

decodeAtiResponse(index)
decodeSriSm(index)
decodeMtFsm(index)

Không dùng:

findTag()

generic nữa.

Đây là cách:

OSS Nokalva
Erlang ASN1
Nokia HLR
Ericsson HLR

đạt throughput rất cao.

Chấm điểm commit hiện tại

Nếu jSS7 gốc là:

100 điểm

thì commit này đưa lên:

250-350 điểm

về decode efficiency.

Nếu áp dụng thêm:

O(1) tag lookup
firstChild/nextSibling
remove parentStack allocation
parse length một lần
generated MAP/CAP decoder

thì mình kỳ vọng thêm 3-5 lần throughput nữa, và lúc đó mục tiêu 1 triệu TPS trên 32 core bắt đầu trở nên thực tế thay vì chỉ là lý thuyết.