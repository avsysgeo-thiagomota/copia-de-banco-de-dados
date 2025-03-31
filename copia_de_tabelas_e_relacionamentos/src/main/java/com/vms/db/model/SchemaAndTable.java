package com.vms.db.model;

public record SchemaAndTable(
        String schema,
        String tableName
)
{
    @Override
    public String toString() {
        return schema + "." + tableName;
    }
}