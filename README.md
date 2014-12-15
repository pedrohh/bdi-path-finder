Travis integration
===============
https://magnum.travis-ci.com/pedrohh/bdi-path-finder.svg?token=pD5UfskbjibH3g2CriG5&branch=master

T07 – Determinação de Percursos usando Agentes BDI
===============

Implementação de um agente BDI capaz de aconselhar o condutor sobre o melhor percurso a 
realizar numa viagem de automóvel. 

Descrição
===============

Implemente um agente BDI capaz de aconselhar o condutor sobre o melhor percurso a realizar 
numa viagem de automóvel entre um ponto origem e um ponto destino a especificar. 
A intenção do condutor pode ser chegar rapidamente ao destino (trabalho) ou passar pelas 
zonas mais aprazíveis (passeio). 
O mapa de estradas e os locais de interesse (praias, parques, florestas, monumentos,…) são 
conhecidos à priori. O agente BDI que representa o condutor não conhece antecipadamente as 
condições atuais das estradas (em obras, interrompida), mas pode conhecer esta informação 
quando tal troço de estrada se encontra no seu raio de visão ou através do rádio. 
Considere a existência de um agente “Mundo” que contém toda a informação sobre o estado 
do mundo e informa o agente BDI. Considere também a existência de outros condutores 
(agentes) que podem interagir entre si enviando informação relevante: condição da estrada, 
condições atmosféricas, comentários sobre determinado local, … 
Pode assumir outras informações que considerar relevantes (condições atmosféricas,…). 
O programa deve permitir a simulação de diferentes cenários a especificar pelo utilizador.

Material
===============
Jadex

Wiki
===============
http://paginas.fe.up.pt/~eol/AIAD/jadex/doku.php
