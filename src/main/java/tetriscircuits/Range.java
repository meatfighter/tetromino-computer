package tetriscircuits;

public class Range {
    
    private final String str;
    private final Integer num;
    private final Integer num2;
    
    public Range(final Integer num) {
        this(null, num, null);
    }
    
    public Range(final Integer num, final Integer num2) {
        this(null, num, num2);
    }

    public Range(final String str, final Integer num, final Integer num2) {
        this.str = str;
        this.num = num;
        this.num2 = num2;
    }

    public String getStr() {
        return str;
    }

    public Integer getNum() {
        return num;
    }

    public Integer getNum2() {
        return num2;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str);
        }
        if (num != null) {
            sb.append(num);
        }
        if (num2 != null) {
            sb.append("..").append(num2);
        }
        return sb.toString();
    } 
}