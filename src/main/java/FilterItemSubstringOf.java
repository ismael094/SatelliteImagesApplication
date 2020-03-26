class FilterItemSubstringOf implements FilterItem {
    private final String property;
    private final String value;

    public FilterItemSubstringOf(String property, String value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public String getString() {
        return "substringof('"+value+"',"+property+")";
    }
}