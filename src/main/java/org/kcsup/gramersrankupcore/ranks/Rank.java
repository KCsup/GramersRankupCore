package org.kcsup.gramersrankupcore.ranks;

public class Rank {
    private String name;
    private String prefix;
    private String chatPrefix;
    private int weight;

    public Rank(String name, String prefix, String chatPrefix, int weight) {
        this.name = name;
        this.prefix = prefix;
        this.chatPrefix = chatPrefix;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }

    public int getWeight() {
        return weight;
    }
}
