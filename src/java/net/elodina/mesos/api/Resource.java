package net.elodina.mesos.api;

import com.google.protobuf.GeneratedMessage;
import net.elodina.mesos.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class Resource extends Message {
    private String name;
    private Value value;
    private String role = "*";

    public Resource() {}
    public Resource(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public Resource(String s) {
        // cpus(kafka):0.5
        // mem:1024
        // ports:0..100,110..200

        int colon = s.indexOf(":");
        if (colon == -1) throw new IllegalArgumentException(s);

        String name = s.substring(0, colon);
        String value = s.substring(colon + 1);

        int bracket = name.indexOf("(");
        if (bracket != -1) {
            role = name.substring(bracket + 1, name.length() - 1);
            name = name.substring(0, bracket);
        }

        this.name = name;
        this.value = new Value(value);
    }

    public String name() { return name; }
    public Resource name(String name) { this.name = name; return this; }

    public Value value() { return value; }
    public Resource value(Value value) { this.value = value; return this; }

    public String role() { return role; }
    public Resource role(String role) { this.role = role; return this; }

    @Override
    public org.apache.mesos.Protos.Resource proto0() {
        org.apache.mesos.Protos.Resource.Builder builder = org.apache.mesos.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.Protos.Value.Type.valueOf(value.type().name()));
        switch (value.type()) {
            case SCALAR:
                builder.setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder().setValue(value.asDouble()));
                break;
            case RANGES:
                builder.setRanges(ranges0(value.asRanges()));
                break;
            default:
                throw new IllegalStateException("unsupported type " + value.type());
        }

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto0(GeneratedMessage message) {
        org.apache.mesos.Protos.Resource resource = (org.apache.mesos.Protos.Resource) message;
        name = resource.getName();

        Value.Type type = Value.Type.valueOf(resource.getType().name());
        Object value = null;
        if (resource.hasScalar()) value = resource.getScalar().getValue();
        if (resource.hasRanges()) value = ranges0(resource.getRanges());
        if (value == null) throw new IllegalStateException("no value");
        this.value = new Value(type, value);

        role = resource.getRole();
        return this;
    }

    @Override
    public org.apache.mesos.v1.Protos.Resource proto1() {
        org.apache.mesos.v1.Protos.Resource.Builder builder = org.apache.mesos.v1.Protos.Resource.newBuilder();
        builder.setName(name);

        builder.setType(org.apache.mesos.v1.Protos.Value.Type.valueOf(value.type().name()));
        switch (value.type()) {
            case SCALAR:
                builder.setScalar(org.apache.mesos.v1.Protos.Value.Scalar.newBuilder().setValue(value.asDouble()));
                break;
            case RANGES:
                builder.setRanges(ranges1(value.asRanges()));
                break;
            default:
                throw new IllegalStateException("unsupported type " + value.type());
        }

        builder.setRole(role);
        return builder.build();
    }

    @Override
    public Resource proto1(GeneratedMessage message) {
        org.apache.mesos.v1.Protos.Resource resource = (org.apache.mesos.v1.Protos.Resource) message;
        name = resource.getName();

        Value.Type type = Value.Type.valueOf(resource.getType().name());
        Object value = null;
        if (resource.hasScalar()) value = resource.getScalar().getValue();
        if (resource.hasRanges()) value = ranges1(resource.getRanges());
        if (value == null) throw new IllegalStateException("no value");
        this.value = new Value(type, value);

        role = resource.getRole();
        return this;
    }

    public int hashCode() {
        return 31 * (31 * name.hashCode() + value.hashCode()) + role.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Resource)) return false;
        Resource other = (Resource) obj;
        return name.equals(other.name) && value.equals(other.value) && role.equals(other.role);
    }

    public String toString(boolean _short) {
        String s = "";

        s += name;
        if (!role.equals("*")) s += "(" + role + ")";
        s += ":" + value;

        return s;
    }

    public static List<Resource> parse(String s) {
        // cpus:0.5;ports:10..20,30..40;mem:1024
        List<Resource> resources = new ArrayList<>();

        for (String r : s.split(";")) {
            r = r.trim();
            if (!r.isEmpty())
                resources.add(new Resource(r));
        }

        return resources;
    }

    public static String format(List<Resource> resources) {
        return Strings.join(resources, ";");
    }
}
