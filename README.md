# Football players similarity search
#### Course project, Scala elective, Fall 2021, Innopolis University
Lev Svalov, l.svalov@innopolis.university

## Introduction

The advanced statistic in professional sports have been enourmously hyped recently. Due to the growth of different metrics by which some sport displine is evaluated, the data analysis have become the powerful must-have tool in every succesfull sport organisation. <br>
Perhaps you are familiar with the movie 
[`Moneyball` (or 'Человек, который изменил все' in Russian)](https://en.wikipedia.org/wiki/Moneyball_(film)). 
It's based on real story where one smart guy succeed to manage the team with small resources by applying the data analysis. 
Roughly speaking, this is what my project about. By using the naive data science clustering and clasification algorithms, the similiary between players can be retrieved. <br>
So that, it can be applied for managment problems in sport such that: My club hero Leo Messi is leaving the club. How to find someone who at least somehow is playing like him? Probably, this analysis is handy there. 

**Disclaimer**: I'm quite dumb and getting a serious lack of time. So that, for now, there are very naive approaches to the analysis and functionalities with doubtful quality of code in terms of Scala and FP paradigms.
I'm fully aware of it. Sorry for that :)

## Functionality description and used libraries
The project functionalities are relying on the one idea:
there is dataset with football players consisting the vector of features for every player. Doing some analysis of features, we would like to present to correlation between the players. This is pretty much it. 
To acomplish that, I have used the following libraries:
1. `Doobie` - interaction with the database. 

     The data about the players is stored in format of `sqlite` dump file and is structured in 2 tables:
    * Player information - general information, such that: Nationality, Club, Age, Preffered position on football pitch
    * Player skills - values for each parrticular skill of the player. 
    Total number of skills for each player - 36. It describes as physical abilities (like Speed or Dribbling), as well as the mental       strenghts (like Aggresion). 
    The scheme is: `id | player_id | skill_name | skill_value` 
2. `Http4s, Circe` - http server

    The implementation of the server is quite straightforward. Several http routes are declared using `Http4sDsl` and for corresponding     route there is an action that somehow interacts with db and provide the appropriate fucntionality.
3. `Spark` - analysis of similiraties of player's vectors
    
    I did not implement the algorithms by myself but took it from the machine learning part of the `Spark` library. 2 main things that     I have borrowed: approximate kNN and clustering algorithms.
    
#### Functionalities:
1. `similiar players` 
Suggest k players who can substitute the given player

    **http endpoint**: `/player?id={ID}&k={K}`, where
    * ID - id of the desired player, integer value
    * K - number of suggested players you wish to obtain. 
    Optional parameter, the default = `5`
2. `cluster players`
    Provide grouping of the database players according to their skills. It can be     handy for a broad analysis of players without sticking to the particular         specialties of one target player.
    
    **http endpoint**: `/cluster?n={N}`, where
    * n - number of cluster by which players should be grouped, integer value. Optional, the default number of groups - `5`
3. `next Messi`
    Suggest the 5 similiar players who can substitute Leo Messi.
     **http endpoint**: `/nextMessi`
4. `next Ronaldo`
    Suggest the 5 similiar players who can substitute Cristiano Ronaldo.
     **http endpoint**: `/nextRonaldo`
#### Some examples
There are a couple of screenshots with the queries 
![](https://i.imgur.com/P7tZPz6.png)
![](https://i.imgur.com/PYOBs15.png)


#### Tests
Yeah, I acknowledge that there are no explicit tests in my project, but I can ensure you that the bunch of smoke tests were carried out ;)

## How to start
I have created the Dockerfile, so there are 2 ways to test the project.
The first way:
1. Clone the project
2. Run the `Server.scala` with option `Include dependencies with Provide scope`
3. Enjoy requsting the server with descripted above queries from the localhost.

The second is to build Docker image and run the container:
1. Build the image using the Dockerfile - `docker build .`
2. Run the container `docker run -p 8080:8080 {containerID}`
3. Enjoy requsting the server with descripted above queries from the docker container.

## Final words

Well, this is the project. The only 2 things I wanted to add: 
- to refer to [this quote](https://knowyourmeme.com/memes/but-its-honest-work) which describes my project naturally.
- to thank you for the course that was interesting and time-consuming. I have mostly suffered due to my own work and other studies, but overall it was a nice experience. 