# Compose e dependências associadas fixadas ao teto do compileSdk 36

O projeto está em AGP 9.0.1 com `compileSdk` 36.1, e várias bibliotecas do ecossistema já publicaram versões que exigem `compileSdk` 37 — e, com ele, o AGP 9.1. Isto colocou-nos numa janela estreita: as versões mais recentes não são instaláveis, e as compatíveis têm de ser escolhidas **em conjunto**, não uma a uma. Foram descobertas por tentativa e erro, cada uma a partir de um build falhado.

Fixámos por isso: Compose BOM em `2025.09.00` (o `2026.08.00` exige compileSdk 37/AGP 9.1) e, na mesma família, `activity-compose`, `lifecycle` e `navigation-compose`; e Coil em `3.0.0` (o 3.6.x arrasta Compose 1.12 e o mesmo teto). Abandonámos por completo o `material-icons-extended` pelo mesmo conflito — usamos o conjunto de ícones do core (`Icons.Default.*`, `Icons.AutoMirrored.Filled.*`), que cobriu tudo o que precisámos até agora. Consequência relacionada da mesma escolha de AGP 9: o Kotlin passou a estar embutido no plugin, por isso aplicar `org.jetbrains.kotlin.android` explicitamente parte o build.

Estas versões formam um bloco interligado: subir qualquer uma isoladamente parte o build. **A condição para revisitar é a adoção do AGP 9.1 + compileSdk 37** — nessa altura movem-se todas de uma vez, e valida-se com `./gradlew :app:assembleDebug`, não só com os testes, porque parte destes conflitos só aparece na fase de merge de recursos.

Alternativa considerada e rejeitada: descer para AGP 8.x, para assentar num ecossistema mais estabelecido em vez de estar à frente dele. Rejeitada por ser um retrocesso com custo de migração imediato para resolver um problema que o tempo resolve sozinho — o desconforto aqui é temporário e o custo real é apenas ter de bumpar em grupo.
