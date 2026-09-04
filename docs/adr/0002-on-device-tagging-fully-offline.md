# Tagging de roupa 100% on-device, sem Google Vision API

O plano original usava a Google Vision API (cloud) como tagger primário, com regras locais só como fallback offline. Decidimos que o tagging de `ClothingItem` corre inteiramente on-device, sem dependência de rede nem chamada a serviços de IA na cloud — o offline-first passa a ser um requisito real da app inteira, incluindo o fluxo de adicionar peças, não só do uso diário. Método concreto de tagging on-device ainda por definir.
