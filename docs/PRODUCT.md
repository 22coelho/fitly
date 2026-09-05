# FITLY — o produto

Estado: MVP em construção. Este documento descreve o que **existe hoje**, não o que está planeado.
Para o vocabulário de domínio ver `CONTEXT.md`; para as decisões e o seu porquê, `docs/adr/`.

## O que é

Uma app Android para organizar o guarda-roupa e responder à pergunta "o que visto hoje?".

O utilizador fotografa cada peça de roupa, marca-a com quatro etiquetas, e a app monta
aleatoriamente um outfit a partir dessas peças. Cada sugestão é aceite ou rejeitada, e fica
registada no histórico.

Três restrições definem o produto (ADRs 0001 e 0002):

- **Android-only.** Sem KMP, sem iOS.
- **100% offline.** Zero chamadas de rede — sem serviços cloud, sem API de meteorologia.
- **Sem machine learning.** O gerador é aleatoriedade pura dentro do filtro escolhido; não aprende
  com o que o utilizador aceita ou rejeita. A extração de cor é análise de imagem no dispositivo
  (Palette), não ML.

## Modelo em termos de produto

**ClothingItem** — uma peça. Tem uma foto (obrigatória), uma cor dominante extraída
automaticamente da foto, e quatro etiquetas, todas obrigatórias:

| Etiqueta | Valores |
|---|---|
| Tipo | `TOP`, `BOTTOM`, `SHOES`, `DRESS`, `ACCESSORY` |
| Ocasião | `CASUAL`, `WORK`, `SPORT`, `FORMAL`, `DATE` |
| Estação | `SUMMER`, `WINTER`, `ALL_YEAR` |
| Condição | `NEW`, `GOOD`, `WORN` |

**Outfit** — uma combinação de peças com quatro posições (*slots*):

- **Top + Bottom + Shoes são obrigatórios.** Um `DRESS` preenche Top e Bottom em simultâneo.
- **Accessory é opcional** — entra se houver acessórios elegíveis.
- Tem um estado (`PENDING` enquanto está no ecrã, depois `ACCEPTED` ou `REJECTED`) e uma flag de
  favorito.

Não existe agrupamento de peças nem de outfits — são listas planas. O conceito de "coleção" foi
rejeitado para o MVP.

## Ecrãs

Cinco ecrãs. Três são separadores fixos na barra inferior; dois são empurrados por cima do
separador Wardrobe (e escondem a barra inferior enquanto estão abertos, para que um toque acidental
num separador não descarte uma edição a meio).

A app arranca no **Wardrobe**, não no Home — sem peças não há nada a gerar.

Todos os textos visíveis estão em português; os valores das etiquetas aparecem tal como estão no
código (`ALL_YEAR`, `WORN`, ...), ainda sem tradução.

### 1. Wardrobe — separador, ecrã inicial

A lista de todas as peças. Cada linha mostra a miniatura da foto, o tipo como título, e
`OCASIÃO • ESTAÇÃO • CONDIÇÃO` por baixo.

- Três filtros em linhas de chips no topo: **Tipo**, **Ocasião**, **Estação**. Cada um tem uma
  opção "todos" para não filtrar. Combinam-se entre si (E lógico).
- Botão flutuante `+` → Adicionar peça.
- Tocar numa linha → Detalhe da peça.
- Sem peças: "Ainda não há peças na wardrobe."

Não há filtro por Condição, nem pesquisa por texto.

### 2. Adicionar peça — empurrado

Formulário para criar uma peça.

- **Escolher foto** abre o *Android Photo Picker* (não a câmara — ver Lacunas). Ao escolher, a foto
  é copiada para o armazenamento interno da app e a cor dominante é extraída nesse momento.
- Quatro linhas de chips, uma por etiqueta, sem opção "todos" — são campos obrigatórios.
- **Guardar** só fica ativo quando a foto e as quatro etiquetas estão todas preenchidas. Ao guardar,
  volta ao Wardrobe.

### 3. Detalhe da peça — empurrado

O mesmo formulário, pré-preenchido com a peça existente.

- Permite trocar a foto e alterar qualquer etiqueta, com **Guardar**.
- **Apagar** pede confirmação num diálogo ("Esta ação não pode ser desfeita").
  Apagar uma peça apaga em cascata todos os outfits que a usavam num slot obrigatório; se a peça só
  era o acessório do outfit, o outfit sobrevive e fica sem acessório (ADR 0005).

### 4. Home — separador

O ecrã do "o que visto hoje?".

- Uma linha de chips de **Ocasião** (com "todos") que restringe as peças elegíveis para a geração.
- **Gerar outfit** monta uma sugestão e mostra as fotos das peças lado a lado.
- Com um outfit no ecrã: **Aceitar**, **Rejeitar**, e um coração para marcar como favorito.
  Aceitar e rejeitar gravam o estado e limpam o ecrã; o outfit passa a constar no histórico.
- Se o guarda-roupa não conseguir preencher os slots obrigatórios: "Sem peças suficientes na
  wardrobe para gerar um outfit."
- Sem nada gerado: "Toca em Gerar para veres uma sugestão."

**Aceitar e rejeitar não têm consequência funcional diferente** — como não há aprendizagem, a
distinção é apenas o registo que fica no histórico.

### 5. Histórico — separador

A lista dos outfits já decididos, do mais recente para o mais antigo. Cada linha mostra as
miniaturas das peças, o estado (`ACCEPTED` / `REJECTED`) e o coração de favorito, que pode ser
alternado a partir daqui.

Os outfits ainda por decidir (`PENDING`) não aparecem. A lista é só de leitura fora do favorito —
não se apaga nem se reabre um outfit. Sem histórico: "Ainda não há outfits no histórico."

## Lacunas conhecidas

Coisas que um leitor assume que existem, mas não existem — algumas por decisão, outras por estarem
a meio.

**Adiado por decisão:**

- **Não há câmara dentro da app.** As fotos vêm do Photo Picker do sistema. A integração com
  CameraX está explicitamente fora de âmbito e não deve ser iniciada sem pedido.
- **Sem pesquisa** no guarda-roupa.
- **Sem coleções/grupos** de peças ou de outfits.
- **Sem aprendizagem** a partir do aceitar/rejeitar, e **sem meteorologia**.

**Construído mas não ligado — vale a pena decidir o que fazer:**

- **A cor dominante é extraída e guardada em cada peça, mas nunca é mostrada.** Nenhum ecrã a usa.
- **A Estação e a Condição são etiquetas obrigatórias que o gerador ignora por completo** — só a
  Ocasião filtra a geração. Na prática, um casaco de `WINTER` pode ser sugerido a par de uma peça de
  `SUMMER`. A Estação pelo menos serve de filtro no Wardrobe; a Condição hoje não faz nada em lado
  nenhum depois de ser gravada.
- **Marcar favoritos não leva a lado nenhum.** Dá para marcar no Home e no Histórico, mas não há
  forma de filtrar ou listar só os favoritos.
- `ClothingType.fillsSlots()` existe e está testado no domínio, mas o gerador não o usa — tem a sua
  própria lógica para o caso do vestido.

**Ainda não abordado:**

- A composição de um outfit não é editável: não se troca uma peça de uma sugestão, só se gera outra.
- Não há forma de repetir/vestir de novo um outfit do histórico.
- Sem ecrã de definições, sem onboarding, sem estados de erro além de mensagens curtas
  (snackbar com o nome técnico do erro, ex.: `DISK_FULL`).
