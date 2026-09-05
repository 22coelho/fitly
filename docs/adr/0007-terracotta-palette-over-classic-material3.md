# Paleta terracota gerada, sobre Material 3 clássico

A UI era o Material 3 por omissão — roxo baseline, tipografia default, `themes.xml` ainda com o
template roxo/teal do Android Studio a pintar a janela antes do Compose arrancar. Escolhemos dar-lhe
identidade própria sem que o chrome compita com o conteúdo, que é fotografia de roupa colorida.

**A paleta é gerada, não escolhida à mão.** Seed terracota `#B4522F` (L\* 46.9, C 54.0, hue 46° em
Lab). Dele saem seis paletas tonais — primary, secondary, tertiary, neutral, neutral variant e error
— e todos os ~36 papéis de cada esquema são preenchidos a partir delas. Preencher *todos* é
deliberado: os componentes do Material 3 leem muito mais papéis do que os que reconhecemos à vista
(`surfaceContainerHigh` num `Card`, `secondaryContainer` num `FilterChip` selecionado), e qualquer
papel deixado por definir volta a ser roxo baseline no primeiro sítio onde não olhámos.

As superfícies são **neutros quentes**: o hue do terracota com croma 3.5, o que dá um off-white
acastanhado no claro e um carvão quente no escuro. O laranja saturado fica reservado para ação.

**Dois desvios ao mapeamento standard do Material 3**, ambos medidos e não estéticos:

- O `primary` claro é o **tom 48**, não o tom 40 da regra. Qualquer laranja no tom 40 lê-se como
  castanho, e o objetivo era que a app parecesse laranja no tema em que a maioria a vai abrir. O tom
  50 era a intenção inicial mas falha o WCAG AA contra branco por 0,02 (4,48:1); o 48 passa a
  4,81:1. Texto escuro por cima não é alternativa — tom 10 sobre tom 50 dá apenas 3,82:1.
- O `error` é uma paleta de **hue 16°**, 30° afastada do seed, em vez do vermelho default do
  Material (hue ~37°), que a esta distância do terracota seria quase indistinguível. Isto importa no
  diálogo de confirmação de apagar uma peça, onde o botão primário e o destrutivo aparecem juntos.

## Material 3 Expressive não está disponível — e não é uma questão de bump

A intenção inicial era adotar o `MaterialExpressiveTheme` com `motionScheme` e `ButtonGroup`.
Não é possível nesta janela de dependências:

- O BOM `2025.09.00` (fixado pelo ADR 0006) resolve **material3 1.3.2**, onde nada disso existe.
- Forçar `material3:1.4.0` por cima do BOM **compila e resolve sem exigir compileSdk 37** — o que
  contraria a suposição do ADR 0006 de que todo o ecossistema puxa o teto para cima ao mesmo tempo —
  mas não traz nada: `MaterialExpressiveTheme`, `ExperimentalMaterial3ExpressiveApi` e `MotionScheme`
  são `internal` nessa versão, e `ButtonGroup`, `LoadingIndicator` e `MaterialShapes` não existem no
  seu API público. Em troca, o 1.4.0 deixa cair a dependência transitiva de `material-icons-core`,
  o que parte todos os `Icons.Default.*` do projeto.

Ficamos portanto em `MaterialTheme` clássico. Onde o Expressive ia entrar, os substitutos que
existem em 1.3.2 são `SingleChoiceSegmentedButtonRow` + `SegmentedButton` (verificados a compilar) e
`CircularProgressIndicator`.

**A condição para revisitar continua a ser a do ADR 0006** — AGP 9.1 + compileSdk 37 — com a nota de
que a barreira ao Expressive não é o compileSdk, é o API público do material3 ainda o esconder.

## Consequências

A regeneração da paleta é um passo determinístico a partir do seed, não uma edição manual do
`Color.kt`: os tons são L\* em LCh(ab) com o croma reduzido até caber no gamut sRGB. O `themes.xml`
passa a `Theme.Material3.DayNight.NoActionBar` e espelha `surface` e `primary` em `colors.xml`,
para o arranque não piscar uma cor diferente da do primeiro frame.
