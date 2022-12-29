package org.kcsup.gramersrankupcore.ranks;

public class Rank {
    private final String name;
    private final String prefix;
    private final String chatPrefix;
    private final int weight;

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
