package gov.Lab3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Function;

public class Network {
    HashSet<Profile> members;

    public Network() {
        members = new HashSet<>();
    }

    public HashSet<Profile> getProfiles() {
        return members;
    }

    public Network addProfile(Profile profile) {
        members.add(profile);
        return this;
    }

    public Network removeProfile(Profile profile) {
        members.remove(profile);
        return this;
    }

    public Network addRelation(Profile profile1, Profile profile2, Relationship relationship) {
        if (members.contains(profile1) && members.contains(profile2)) {
            profile1.addRelation(profile2, relationship);
            profile2.addRelation(profile1, relationship);
        }else {
            throw new IllegalArgumentException("Both profiles must be in the network.");
        }
        return this;
    }

    public Network removeRelation(Profile profile1, Profile profile2) {
        if (members.contains(profile1) && members.contains(profile2)) {
            profile1.removeRelation(profile2);
            profile2.removeRelation(profile1);
        }else {
            throw new IllegalArgumentException("Both profiles must be in the network.");
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        members.stream()
                .sorted((p1, p2) -> Double.compare(p2.getImportance(), p1.getImportance()))
                .forEach(profile -> {
                    sb.append(profile.getName()).append(" (")
                      .append(profile.getImportance()).append("):\n\t");
                    for (Profile relation : profile.getRelations().keySet()) {
                        sb.append(relation.getName()).append(", ");
                    }
                    sb.append("\n");
                });
        return sb.toString();
    }
}
