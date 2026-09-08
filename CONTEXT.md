# FITLY

App de organização de guarda-roupa: catalogar peças de roupa por foto e sugerir outfits diários. MVP é Android-only, 100% offline.

## Language

**Wardrobe**:
O conjunto completo de `ClothingItem` de um utilizador dentro da app.
_Avoid_: Guarda-roupa (usar Wardrobe mesmo em conversa PT, para manter um único termo no código e na documentação), Closet

**ClothingItem**:
Uma peça de roupa individual, adicionada por foto. A cor é extraída automaticamente da foto (cor dominante, sem ML); tipo pode vir pré-preenchido por Type Suggestion mas fica sempre editável, e Occasion, estação e condição são sempre escolhidos manualmente pelo utilizador de uma lista fixa. Cada ClothingItem preenche um slot no Outfit: Top, Bottom ou Shoes — exceto Vestido, que preenche Top+Bottom em simultâneo.
_Avoid_: Item, Peça (usar ClothingItem no código; "peça" é aceitável em conversa), auto-tagging por ML (termo mais amplo do domínio, continua rejeitado/adiado — ver Type Suggestion para o que existe hoje, e ADR 0008)

**Type Suggestion**:
Um palpite de melhor esforço para o campo tipo de um ClothingItem, a partir da foto escolhida, usando o ML Kit Image Labeling (a inferência corre on-device; a biblioteca em si envia telemetria própria de utilização, sem opt-out — nunca a foto nem o resultado, ver ADR 0008). Cobertura parcial e deliberadamente inconsistente entre categorias — só alguns rótulos mapeiam sem ambiguidade para tipo (ver ADR 0008). Só preenche o campo quando está vazio, nunca substitui um valor já lá; fica sempre editável e visualmente indistinguível de uma escolha manual.
_Avoid_: Sugestão de Tipo (usar Type Suggestion no código; "sugestão de tipo" é aceitável em conversa), auto-tagging (termo mais amplo do domínio — ver ClothingItem)

**Occasion**:
Vocabulário fixo e partilhado, usado tanto como tag do `ClothingItem` como filtro no Outfit Generator — o mesmo campo em ambos, sem camada de mapeamento. Lista inicial: Casual, Trabalho, Desporto, Formal, Encontro (expansível mais tarde).
_Avoid_: Ocasião (usar Occasion no código; "ocasião" é aceitável em conversa)

**Outfit**:
Uma combinação de `ClothingItem` proposta pelo Outfit Generator ou guardada a partir do Histórico, com slots obrigatórios Top + Bottom + Shoes e um Accessory opcional. Não existe agrupamento curado de Outfits nem de ClothingItems no MVP — são uma lista plana. Tem uma flag `favorite` para o utilizador marcar para reutilizar.
_Avoid_: Look, Coleção (o conceito de "Coleção"/grupo de peças do doc original foi rejeitado para o MVP)

**Outfit Generator**:
A funcionalidade que seleciona aleatoriamente um `ClothingItem` elegível para cada slot obrigatório de um Outfit (Top, Bottom, Shoes), filtrando por Occasion pedida quando aplicável. No MVP é aleatoriedade pura dentro do filtro — sem aprendizagem com aceitar/rejeitar do utilizador, sem chamada a serviços de IA na cloud, e sem Weather API (nenhuma chamada de rede).
_Avoid_: IA, motor de IA (o doc original chama a isto "IA" — evitar esse termo no código/docs do MVP para não sugerir machine learning onde há apenas seleção aleatória)

**History**:
O registo de Outfits gerados pelo Outfit Generator, cada um marcado como aceite ou rejeitado pelo utilizador.
_Avoid_: Histórico (usar History no código; "histórico" é aceitável em conversa)
