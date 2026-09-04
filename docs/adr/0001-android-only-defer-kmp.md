# Android-only para já, KMP adiado

O plano original previa Kotlin Multiplatform (iOS + Android) desde a Semana 1. Decidimos avançar como projeto Android puro (single module, sem `commonMain` nem alvo iOS) e só considerar KMP mais tarde, se/quando houver necessidade real de iOS. Retrofit de KMP num app Android maduro implica reestruturar módulos e reescrever a camada de dados/DI para partilha; aceitamos esse custo futuro em troca de velocidade agora, enquanto o produto ainda não está validado.
