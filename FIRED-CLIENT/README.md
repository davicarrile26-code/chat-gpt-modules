# FIRED CLIENT

Projeto independente de mod client-side para Minecraft 1.8.9 + Forge.

## Requisitos
- JDK 8 (Minecraft Forge 1.8.9 / ForgeGradle 2.1)
- Gradle 2.14.x
- Forge 1.8.9-11.15.1.2318

## Compilar
No diretório do projeto:
```bash
gradle setupDecompWorkspace
gradle build
```
O JAR será gerado em `build/libs/`.

## Estado inicial
- Estrutura Forge 1.8.9
- Gerenciador de módulos
- GUI simples (Right Shift)
- Primeiro módulo: FPS
- Os demais módulos serão implementados por etapas.

Este projeto é uma reimplementação independente. Não inclui código binário do Flick Client.
