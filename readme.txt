===================================
PROJECT: IJA
DATE: 04.2020
TEAM: Pavel Yadlouski (xyadlo00)
      Oleksii Korniienko (xkorni02)
===================================

DESCRIPTION:
    Aim of this project is to create application that would show
    movement of transport on map in real time. Map and transport
    lines are readied from XML files map.xml and transport.xml
    respectively. Every bus run in its own thread.

RUN:
    ant compile
    ant run

REQUIREMENTS:
    At list JRE 8 (with build in JavaFX libraries)

LIMITATIONS:
    * Each line has online on bus