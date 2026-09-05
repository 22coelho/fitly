# Fitly

App Android de guarda-roupa: fotografas cada peça de roupa, marcas com quatro etiquetas, e a app
sugere aleatoriamente um outfit a partir do que tens.

- **Android-only**, sem KMP nem iOS.
- **100% offline** — zero chamadas de rede.
- **Sem machine learning** — o gerador é aleatoriedade pura dentro dos filtros escolhidos; a cor
  dominante de cada peça é análise de imagem no dispositivo (Palette), não ML.

Para uma descrição completa dos ecrãs e do que existe hoje, ver [docs/PRODUCT.md](docs/PRODUCT.md).
Para o vocabulário do domínio, ver [CONTEXT.md](CONTEXT.md). Para o porquê das decisões, ver
[docs/adr/](docs/adr/).

## Stack

Kotlin + Jetpack Compose, Material 3, Room, Koin, Coil. Arquitetura MVI de camada única
(`domain/` → `data/` → `presentation/` → `di/`), documentada em [CLAUDE.md](CLAUDE.md).

## Correr o projeto

```bash
./gradlew :app:assembleDebug        # gera o APK de debug
./gradlew :app:testDebugUnitTest    # corre a suite de testes unitários
```

Requer o `compileSdk` e o AGP fixados em [gradle/libs.versions.toml](gradle/libs.versions.toml) —
ver [ADR 0006](docs/adr/0006-pin-compose-stack-to-compilesdk-36.md) antes de mexer em versões.
