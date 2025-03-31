/*Um record é um tipo especial de classe introduzido no Java 14 (como preview) e estável a partir do Java 16.
Ele serve para representar dados imutáveis, ou seja: objetos que apenas carregam valores e não têm lógica de comportamento complexa.

É sempre imutável
Campos são final automaticamente
Sem setters
Segurança contra mudanças acidentais
Gera automaticamente:
- equals() e hashCode() (com base nos campos)
- toString() (você pode sobrescrever se quiser)
 - Getters no estilo nomeDoCampo() (sem o get)
 */

package com.vms.db.model;

public record ForeignKeyInfo(
        SchemaAndTable tabelaOrigem,
        String colunaOrigem,
        SchemaAndTable tabelaReferencia,
        String colunaReferencia
) {
    @Override
    public String toString() {
        return tabelaOrigem + "." + colunaOrigem + " -> " + tabelaReferencia + "." + colunaReferencia;
    }
}
