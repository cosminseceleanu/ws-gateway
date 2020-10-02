[![reviewdog](https://github.com/cosminseceleanu/ws-gateway/workflows/reviewdog/badge.svg?branch=master&event=push)](https://github.com/cosminseceleanu/ws-gateway/actions?query=workflow%3Areviewdog+event%3Apush+branch%3Amaster)
[![codecov](https://codecov.io/gh/cosminseceleanu/ws-gateway/branch/master/graph/badge.svg)](https://codecov.io/gh/cosminseceleanu/ws-gateway)


# WSGateway - WebSocket API Gateway

- [Documentation](https://cosminseceleanu.github.io/ws-gateway/#/)
- [Rest Api Reference](https://cosminseceleanu.github.io/ws-gateway/rest-api-reference)
- [Contributing and development guides](https://cosminseceleanu.github.io/ws-gateway/#/contributing/)



--------------------------

Flow-uri regular user
- Ca si utilizator as vrea imi pot gasi un personal trainer pentru antrenamente live
- Ca si utilizator as vrea imi pot gasi un personal trainer pentru antrenamente online si plan de mancare
- Fiecare trainer are un scor
- Cautare dupa tip antrenament
- Chat cu coach - doar daca a cumparat un plan
- Dupa achizitionarea unui plan poate da review unui coach

Flow-uri coach

- Isi poate defini mai multe membership plans(max 5)
- Definire clase de antrenament. Fiecare clasa poate avea photos/videos per zi(sau nu) si poate sa includa si un meal plan 
- Poate asocia clasele cu un membership plan. Constrangere: o clasa cu meal plan poate apartine doar unui membership pe bani


Monetizare:

- Comision din pachetele coach-urilor - (20% only fans) - putem incepe cu 10% si pe viitor putem mari
- Next step: boost coach Signed-off by @AlexOlteanu

ToDo:

- Research about legal stuff - how coaches will get their money
- Find a good name

- Create XD - nice to have
- Define domain models
- Create git repo and base projects

Domain Objects

User
    - .....


- Trainer/Coach
   - bio
   - pictures
   - Location(City)
   - nickname

- Trainer Reviews   
   
- Membership Plan
 - Classes
 - Price -> defined by Coach with a min and max

- Class
    - Name
    - Short description
    - Pictures and Videos
    - details about practice
    - type(Cardio, Strength, MuscleGain...)