package com.vms.db.model;

import java.util.List;

public record SchemaTables(
        String schema,
        String tableNames
) {}