# FIRED CLIENT

Mod client-side independente para Minecraft 1.8.9 + Forge, com identidade visual inspirada em fogo.

## Visual
- Interface escura.
- Faixa superior em degradê vermelho/laranja.
- Destaques em laranja e âmbar.
- ClickGUI com rolagem para navegar pelo catálogo.

## Catálogo
O gerenciador registra 66 entradas de módulos, distribuídas entre HUD, Combat, Movement, Render, World, Cosmetics e Misc.

**Importante:** nesta etapa, FPS é o módulo funcional já implementado. As outras 65 entradas aparecem como **EM BREVE** na interface e ainda precisam receber implementação e testes. Não são anunciadas como funcionalidades prontas.

## Requisitos
- JDK 8 (não apenas JRE)
- Gradle 2.14.x
- Forge 1.8.9-11.15.1.2318

## Compilar
No diretório do projeto:
```bash
gradle setupDecompWorkspace
gradle build
```
O JAR será gerado em `build/libs/`.

## Controles
- Right Shift: abrir a ClickGUI
- Role a roda do mouse sobre a lista: navegar pelos módulos

Este projeto é uma reimplementação independente e não inclui código binário ou trechos proprietários do Flick Client.
