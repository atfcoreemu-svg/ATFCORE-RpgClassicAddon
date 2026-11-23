# ATFCORE RPG Maker clássico

Fork local do EasyRPG Player. O código e os avisos upstream permanecem neste
repositório. A licença do EasyRPG Player (GPL-3.0-or-later) está em COPYING.
O motor RGSS ainda precisa de um porte arm64 integrado a este aplicativo.

A PlayActivity é protegida por permissão signature, confere também o pacote
chamador e só recebe a árvore SAF selecionada para a sessão. O EasyRPG recebe
a árvore em --project-path e grava saves em uma pasta privada persistente,
identificada pela URI. A saída do motor retorna ao ATFCORE. Os controles e
a configuração de RTP são os do EasyRPG Player. Os arquivos de RTP devem
ser fornecidos pelo usuário.

Build de desenvolvimento com JDK 21, SDK 37 e NDK 28.2.13676358, a partir
deste repositório:

```bash
bash builds/android/gradlew -p builds/android :app:assembleDebug \
  "-PtoolchainDirs=/caminho/buildscripts/android/arm64-v8a-toolchain"
```

O prefixo precisa das dependências Android do EasyRPG, incluindo liblcf.
Os scripts de referência estão em https://github.com/EasyRPG/buildscripts.
O APK debug deve usar a mesma chave debug do ATFCORE. Para a distribuição
fora da loja, o APK assinado deve acompanhar o código correspondente,
incluindo commits e patches das dependências, avisos, licenças e instruções
reproduzíveis. Esta integração ainda não foi validada no Android.
