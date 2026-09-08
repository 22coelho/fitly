# Sugestão de Tipo via ML Kit, como sinal fraco e parcial, não um classificador

O ADR 0002 rejeitou a Google Vision API a favor de tagging inteiramente on-device, e deixou o método concreto em aberto ("ainda por definir"). Esta é essa decisão: **ML Kit Image Labeling**, na variante *bundled* — modelo empacotado no APK, sem download em runtime, a inferência em si corre inteiramente no dispositivo. A excepção de telemetria da própria biblioteca está documentada em baixo — não é "sem chamada de rede em momento algum" de forma literal, mas a promessa que motivou o ADR 0002 (nenhuma foto nem dado do domínio sai do dispositivo) mantém-se.

Investigámos as alternativas antes de decidir. Não existe hoje um modelo pré-treinado, empacotável e com licença comercial clara para classificar tipo de peça nas nossas 5 categorias (`TOP`, `BOTTOM`, `SHOES`, `DRESS`, `ACCESSORY`): O DeepFashion2, a fonte de dados mais óbvia para esse fim, é **licenciado só para investigação não-comercial** — inutilizável numa app publicada. O caminho que produziria um classificador fiável para as 5 categorias seria treinar um modelo próprio via MediaPipe Model Maker, curando ~600-800 fotos por categoria — trabalho real de ML, não a integração de uma dependência.

**Escolhemos não fazer esse trabalho agora.** Em vez disso, o ML Kit Image Labeling (um classificador genérico de imagem, não específico de moda) devolve alguns rótulos que mapeiam sem ambiguidade para as nossas categorias — `Dress`/`Gown` → DRESS, `Jeans`/`Shorts`/`Leggings`/`Tights` → BOTTOM, `Jacket`/`Blazer`/`Jersey` → TOP, `Shoe`/`Sneakers` → SHOES, `Necklace`/`Bracelet`/`Tie`/`Scarf`/`Cap`/`Hat`/`Sunglasses` → ACCESSORY. Não há rótulo genérico "Shirt" nem "Pants": uma T-shirt lisa ou uns chinos não disparam nada. Isto é uma cobertura deliberadamente **parcial e inconsistente entre categorias** — aceitámo-lo porque o custo de errar é zero (o campo fica exactamente como ficaria sem esta feature: vazio, à espera de escolha manual) e o ganho, quando acontece, é real.

Regras que tornam esse "sinal fraco" seguro de usar:

- **Limiar de confiança ~0.6**; havendo mais do que um rótulo candidato na mesma foto, vence o de maior confiança — nunca se combinam nem se pede ao utilizador para desempatar.
- **Só pré-preenche um campo Tipo vazio.** Nunca substitui um valor já lá, seja de uma sugestão anterior mantida, seja de escolha manual — mesmo que o utilizador troque de foto depois.
- **Sem sinal visual de "sugerido"**: o chip pré-preenchido é indistinguível de uma escolha manual, e fica sempre editável.
- Um rótulo do ML Kit que não mapeie com confiança para nenhuma das 5 categorias é ignorado — a regra geral para qualquer rótulo futuro ambíguo é **não mapear**.

O âmbito fica deliberadamente estreito: só o campo Tipo. Occasion, Estação e Condição continuam inteiramente manuais — são demasiado subjectivos para inferir de uma fotografia (uma T-shirt preta lisa é "casual" ou "trabalho"? depende de quem a usa, não da peça).

## A excepção de telemetria — descoberta na verificação manual, não no código

A verificação no emulador (não os testes automáticos — nenhum deles toca o ML Kit real, ver a secção de testes na spec) mostrou o SDK a agendar dois envios (`FIREBASE_ML_SDK`, um a 30s, outro a 24h) para os servidores da Google. A [documentação oficial de termos do ML Kit](https://developers.google.com/ml-kit/terms) confirma-o sem ambiguidade: *"The ML Kit APIs also send metrics about the performance and utilization of the APIs in your app to Google."* Não existe opt-out — nem flag de manifest, nem opção no `ImageLabelerOptions.Builder` — é comportamento intrínseco da biblioteca, verificado, não uma falta de configuração nossa.

**O que sai é metadata de utilização da própria biblioteca (desempenho, chamadas à API) — nunca a foto, nunca o rótulo devolvido, nunca nada do domínio da app.** Aceitamos esta excepção porque é a única forma prática de ter classificação de imagem no dispositivo sem cloud Vision API (a alternativa que o ADR 0002 já tinha rejeitado) nem treinar um modelo próprio (rejeitado acima). A promessa "100% offline" do produto passa a ler-se como "os dados do utilizador nunca saem do dispositivo", não "esta biblioteca nunca envia um único pacote" — uma distinção que só esta feature torna necessária, porque é a primeira dependência de terceiros com telemetria própria que o projecto usa.

## Consequências

Se um dia a cobertura parcial se revelar insuficiente e quisermos as 5 categorias cobertas de forma consistente, o caminho é o MediaPipe Model Maker descrito acima — este ADR não o impede, só documenta porque não o fizemos agora. Nesse cenário, os dados de treino têm de ser fotografados/licenciados para uso comercial; o DeepFashion2 continua desqualificado.

Se esta excepção deixar de ser aceitável (por exemplo, se o produto vier a exigir literalmente zero tráfego de rede, sem excepções), a única saída é remover o ML Kit e, com ele, o Type Suggestion inteiro — não há uma variante da biblioteca sem esta telemetria.
