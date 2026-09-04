# Outfit foreign keys: CASCADE nos slots obrigatórios, SET_NULL no acessório

`OutfitEntity` não tinha nenhuma foreign key para `ClothingItemEntity`, por isso apagar uma peça deixava Outfits guardados (favoritos/histórico) a apontar para um id inexistente, sem qualquer sinal. Corrigido com `@ForeignKey` em `topItemId`/`bottomItemId`/`shoesItemId`/`accessoryItemId`.

Escolhemos `CASCADE` para os três slots obrigatórios (Top, Bottom, Shoes) — um Outfit não pode existir sem eles, por isso apagar a peça apaga o Outfit que dependia dela. Para `accessoryItemId` (opcional) escolhemos `SET_NULL` — perder o acessório não deve destruir o resto do Outfit. Alternativa considerada e rejeitada: `RESTRICT` (impedir apagar uma peça enquanto estiver referenciada por algum Outfit) — pareceu-nos mais surpreendente/frustrante para o utilizador do que perder um registo de histórico antigo.
