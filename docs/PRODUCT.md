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

Todos os textos visíveis estão em português, incluindo os valores das etiquetas: o mapeamento de
cada enum do domínio para a string que o utilizador lê vive em `presentation/Labels.kt` e em
`strings.xml`, e o domínio continua sem saber que existe uma UI.

Os três separadores têm um título grande em serifada, fixo no topo e que não colapsa com o scroll.
Os dois ecrãs empurrados têm uma barra pequena com botão de voltar.

### 1. Wardrobe — separador, ecrã inicial

Uma grelha de duas colunas. Cada célula é só a fotografia da peça, num rácio fixo, com uma barra
fina da sua cor dominante por baixo — sem etiquetas escritas, porque a foto já diz o que a peça é.

- **Tipo** fica no ecrã, numa linha de chips com opção "todos": é o filtro que se usa a toda a
  hora.
- **Ocasião** e **Estação** vivem num *bottom sheet*, aberto pela acção "Filtros" na barra de
  título, que mostra entre parênteses quantos desses dois estão ativos. Os três combinam-se entre
  si (E lógico).
- Botão flutuante `+` → Adicionar peça.
- Tocar numa célula → Detalhe da peça.
- Sem peças: estado vazio com botão "Adicionar peça". Com peças mas nenhuma a corresponder aos
  filtros, a mensagem di-lo e não oferece botão nenhum.

Não há filtro por Condição, nem pesquisa por texto.

### 2. Adicionar peça — empurrado

Formulário para criar uma peça.

- **A própria fotografia é o botão**: tocar nela abre o *Android Photo Picker* (não a câmara — ver
  Lacunas). Ao escolher, a foto é copiada para o armazenamento interno da app e a cor dominante é
  extraída nesse momento.
- Quatro campos obrigatórios, sem opção "todos". Tipo e Ocasião são linhas de chips (cinco valores
  cada); Estação e Condição são controlos segmentados, que a três valores dizem melhor "escolhe
  exatamente um".
- **Guardar** só fica ativo quando a foto e as quatro etiquetas estão todas preenchidas. Ao guardar,
  volta ao Wardrobe.

### 3. Detalhe da peça — empurrado

O mesmo formulário, pré-preenchido com a peça existente.

- Permite alterar qualquer etiqueta, com **Guardar**. **A foto não se troca aqui** — não existe
  nenhuma acção para isso, e a foto aparece sem afordância de toque. Trocar a foto de uma peça
  implica apagá-la e voltar a adicioná-la.
- **Apagar** pede confirmação num diálogo ("Esta ação não pode ser desfeita").
  Apagar uma peça apaga em cascata todos os outfits que a usavam num slot obrigatório; se a peça só
  era o acessório do outfit, o outfit sobrevive e fica sem acessório (ADR 0005).

### 4. Home — separador

O ecrã do "o que visto hoje?".

- Uma linha de chips de **Ocasião** (com "todos") que restringe as peças elegíveis para a geração.
- **Gerar outfit** monta uma sugestão e mostra-a como um cartão vertical, lido de cima para baixo
  como se veste: Top, Bottom, Shoes. As peças repartem entre si a altura disponível, para o outfit
  inteiro caber num ecrã sem scroll. Um `DRESS` preenche Top e Bottom e é desenhado uma só vez.
- O acessório, quando existe, aparece numa fila própria mais pequena por baixo — a separação diz
  visualmente que ele é opcional e os outros três não.
- **Aceitar** e **Rejeitar** vivem numa barra fixa no fundo do ecrã; o coração de favorito está
  sobreposto ao cartão, com a cor escolhida por contraste com a peça que tem por baixo.
  Aceitar e rejeitar gravam o estado e limpam o ecrã; o outfit passa a constar no histórico.
- Se o guarda-roupa não conseguir preencher os slots obrigatórios, ou se ainda nada foi gerado,
  aparece um estado vazio a dizer qual dos dois casos é.

**Aceitar e rejeitar não têm consequência funcional diferente** — como não há aprendizagem, a
distinção é apenas o registo que fica no histórico.

### 5. Histórico — separador

A lista dos outfits já decididos, do mais recente para o mais antigo. Cada cartão mostra as fotos
das peças, o estado ("Aceite" / "Rejeitado"), **a data** em que o outfit foi criado, e o coração de
favorito, que pode ser alternado a partir daqui.

Um chip **"Só favoritos"** filtra a lista. É o único sítio onde a flag de favorito leva a algum
lado, e é o único que faz sentido: a flag é do Outfit, não da peça.

Os outfits ainda por decidir (`PENDING`) não aparecem. A lista é só de leitura fora do favorito —
não se apaga nem se reabre um outfit.

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

- **A Estação e a Condição são etiquetas obrigatórias que o gerador ignora por completo** — só a
  Ocasião filtra a geração. Na prática, um casaco de `WINTER` pode ser sugerido a par de uma peça de
  `SUMMER`. A Estação pelo menos serve de filtro no Wardrobe; a Condição hoje não faz nada em lado
  nenhum depois de ser gravada.
- `ClothingType.fillsSlots()` existe e está testado no domínio, mas o gerador não o usa — tem a sua
  própria lógica para o caso do vestido.

**Ainda não abordado:**

- **A foto de uma peça existente não é editável.** O Detalhe da peça edita as quatro etiquetas mas
  não a fotografia; o `AddItem` é o único sítio onde uma foto entra na app. Trocar a foto de uma
  peça implica apagá-la e voltar a adicioná-la.
- A composição de um outfit não é editável: não se troca uma peça de uma sugestão, só se gera outra.
- Não há forma de repetir/vestir de novo um outfit do histórico.
- Sem ecrã de definições e sem onboarding. Os erros são um snackbar com uma frase em português
  (os três valores de `DataError.Local` estão traduzidos), sem retry nem detalhe.
