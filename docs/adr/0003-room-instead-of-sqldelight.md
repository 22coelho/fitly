# Room em vez de SQLDelight

O plano original especificava SQLDelight, escolhido por ser partilhável entre Android e iOS num projeto KMP. Como o MVP é Android-only (ver ADR 0001) e o KMP está adiado, usamos Room: é o standard idiomático para Android puro, com melhor tooling de migrations e integração com Compose/Coroutines. Se o projeto migrar para KMP no futuro, a camada de dados terá de ser reescrita de qualquer forma — não vale a pena pagar agora a fricção do SQLDelight sozinho por essa eventualidade.
