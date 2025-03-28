package com.vms.db.model;

public class ForeignKeyInfo {
    public final String tabelaOrigem;
    public final String colunaOrigem;
    public final String tabelaReferencia;
    public final String colunaReferencia;

    public ForeignKeyInfo(String tabelaOrigem, String colunaOrigem, String tabelaReferencia, String colunaReferencia) {
        this.tabelaOrigem = tabelaOrigem;
        this.colunaOrigem = colunaOrigem;
        this.tabelaReferencia = tabelaReferencia;
        this.colunaReferencia = colunaReferencia;
    }

    @Override
    public String toString() {
        return tabelaOrigem + "." + colunaOrigem + " -> " + tabelaReferencia + "." + colunaReferencia;
    }
}