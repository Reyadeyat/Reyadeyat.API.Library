package net.reyadeyat.api.library.http;

import java.util.Objects;

public class IPRule implements Comparable<IPRule> {
    String ip;
    String rule;
    IPRule(String ip, String rule) {
        this.ip = ip;
        this.rule = rule;
    }

    @Override
    public int compareTo(IPRule obj) {
        return this.ip.compareTo(obj.ip);
    }
    
    @Override
    public boolean equals(Object obj) {
        return compareTo((IPRule) obj) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.ip);
        return hash;
    }
    
    public Boolean block() {
        return rule.equalsIgnoreCase("block");
    }
    
    public Boolean trust() {
        return rule.equalsIgnoreCase("trust");
    }
    
    public Boolean insession() {
        return rule.equalsIgnoreCase("insession");
    }
}
