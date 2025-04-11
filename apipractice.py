# necessary imports to run code
import requests
import json
from pprint import pprint


# provided by alabama mens basketball team for API access
with open("alabama_api.txt", "r") as file:
    CLIENT_ID = file.readline().strip()
    CLIENT_SECRET = file.readline().strip()
    print(CLIENT_ID)
    print(CLIENT_SECRET)

TOKEN_URL = "https://auth.synergysportstech.com/connect/token"

# Files and locations where we store access changing data
TOKEN_FILE = "SynergyAuth.txt"
BOX_SCORE_JSON = "BoxScore.json"
BOX_SCORE_DATA = "BoxScore.txt"
GAME_ID = "67c7597c0d7d2e7999594652"

#How to obtain access token from Synergy using OAuth2.0
def get_access_token(url, client_id, client_secret):
    # make the request and save our two important values
    response = requests.post(
        url,
        data={"grant_type": "client_credentials"},
        auth=(client_id, client_secret),
    )
    token = response.json()["access_token"] # token itself
    duration = response.json()["expires_in"] # saving when token expires for reminders later
    
    # write to a text file and store our newly created token
    with open(TOKEN_FILE, "w") as file:
        file.write(f"Warning: Token expires in {duration} seconds.")
        file.write("\n-----------Token Below-----------\n")
        file.write(token)
    print(response.json())
    return response.json()["access_token"] # return token itself for use in code

def printPlayerStats(boxJS):
    with open(BOX_SCORE_DATA, "w") as file:
        # determine first team listed in the json
        teamOne = boxJS[0]["data"]["team"]["fullName"]
        teamTwo = ""
        switchedTeam = False
        teamOneOReb = 0
        teamTwoOReb = 0
        teamOneDReb = 0
        teamTwoDReb = 0
        file.write(teamOne + "\n")
        playerstats = {}
        for entry in boxJS:
            data = entry["data"] # obtain relevant info from json

            # Logic for knowing when to switch printed team
            if data["team"]["fullName"] != teamOne and not switchedTeam:
                teamTwo = data["team"]["fullName"]
                file.write("\n" + data["team"]["fullName"] + "\n")
                switchedTeam = True

            # Stat retreival from Server json and adding data to our created Json
            playerName = json.dumps(data["player"]["name"]).strip('"')
            statString = f"{playerName}: "
            pjson = {}

            points = int(json.dumps(data["points"]))
            statString += f"{points} PTS, "
            pjson['points'] = points

            assists = int(json.dumps(data["assists"]))
            statString += f"{assists} AST, "
            pjson['assists'] = assists

            astTurnRatio = float(json.dumps(data["assistsTurnover"]))
            statString += f"{astTurnRatio} AST/TO, "
            pjson['AST/TO'] = astTurnRatio

            oreb = int(json.dumps(data["offReb"]))
            statString += f"{oreb} OREB, "
            pjson['oreb'] = oreb

            dreb = int(json.dumps(data["defReb"]))
            statString += f"{dreb} DREB, "
            pjson['dreb'] = dreb

            if not switchedTeam:
                teamOneOReb += oreb 
                teamOneDReb += dreb
            else:
                teamTwoOReb += oreb 
                teamTwoDReb += dreb

            if "ppp" in data:
                ppp = float(json.dumps(data["ppp"])[:4])
            else:
                ppp = "n/a"
            statString += f"{ppp} PPP, "
            pjson['ppp'] = ppp

            ftMade = int(json.dumps(data["ftMade"]))
            ftTotal = int(json.dumps(data["ftAttempt"]))
            statString += f"{ftMade}/{ftTotal} FT, "
            pjson['ftMade'] = ftMade
            pjson['ftTotal'] = ftTotal

            threePointMade = int(json.dumps(data["shot3Made"]))
            threePointTotal = int(json.dumps(data["shot3Attempt"]))
            statString += f"{threePointMade}/{threePointTotal} 3PT, "
            pjson['3ptMade'] = threePointMade
            pjson['3ptTotal'] = threePointTotal

            shotsMade = int(json.dumps(data["fgMade"]))
            shotsTotal = int(json.dumps(data["fgAttempt"]))
            statString += f"{shotsMade}/{shotsTotal} FG, "
            pjson['fgMade'] = shotsMade
            pjson['fgTotal'] = shotsTotal

            time = int(json.dumps(data["secondsPlayed"])) // 60
            statString += f"{time} MIN"
            pjson['min'] = time

            steals = int(json.dumps(data["steals"]))
            pjson['steals'] = int(steals)

            blocks = int(json.dumps(data["blocks"]))
            pjson['blocks'] = int(blocks)

            missedFG = int(json.dumps(data["fgMissed"]))
            pjson['fgMissed'] = int(missedFG)

            missedFT = int(json.dumps(data["ftMissed"]))
            pjson['ftMissed'] = int(missedFT)

            turnover = int(json.dumps(data["turnovers"]))
            pjson['turnovers'] = int(turnover)
            
            shotsBlocked = int(json.dumps(data["blockedFGAs"]))
            pjson['shotsBlocked'] = int(shotsBlocked)

            foul = int(json.dumps(data["fouls"]))
            pjson['fouls'] = int(foul)

            # PER  (Points + Rebounds + Assists + Steals + Blocks) - (Missed Field Goals + Missed Free Throws + Turnovers + Shots Rejected + Fouls) 
            per = points + oreb + dreb + assists + steals + blocks - missedFG - missedFT - turnover - shotsBlocked - foul
            pjson['PER'] = per

            # ORTG Points scored per 100 possessions
            if ppp != "n/a":
                ortg = ppp*100
                pjson['ORTG'] = ortg

            # EFG% [(Field Goals Made + (0.5 * Three-Point Field Goals Made)) / Field Goals Attempted] * 100
            if shotsTotal != 0:
                efg = (shotsMade + (0.5 * threePointMade)) / shotsTotal * 100
                pjson['EFG'] = efg

            # TS%  Points / (2 * (Field Goals Attempted + (0.475 * Free Throws Attempted)))
            if shotsTotal != 0 or ftTotal != 0:
                ts = points / (2 * (shotsTotal + (0.475 * ftTotal))) * 100
                pjson['TrueShooting'] = ts

            pjson['team'] = data["team"]["fullName"]
        
            # Text File Creation and dictionary assginment to player anme for json creation
            file.write(statString +"\n")
            playerstats[playerName] = pjson
        
        #calculate team rebounds for ORB & DRB
        team1Json = {}
        team1Json['Oreb'] = teamOneOReb
        team1Json['Dreb'] = teamOneDReb

        team2Json = {}
        team2Json['Oreb'] = teamTwoOReb
        team2Json['Dreb'] = teamTwoDReb

        # ORB% (Offensive Rebounds / (Offensive Rebounds + Opponent's Defensive Rebounds)) * 100
        # DRB% (Defensive Rebounds / (Defensive Rebounds + Opponent's Offensive Rebounds)) * 100 
        for player in playerstats:
            pjson = playerstats[player]
            if pjson['team'] == teamOne:
                pjson['ORB%'] = pjson['oreb'] / (pjson['oreb'] + teamTwoDReb)
                pjson['DRB%'] = pjson['dreb'] / (pjson['dreb'] + teamTwoOReb)
            else:
                pjson['ORB%'] = pjson['oreb'] / (pjson['oreb'] + teamOneDReb)
                pjson['DRB%'] = pjson['dreb'] / (pjson['dreb'] + teamOneOReb)

        playerstats[teamTwo] = team2Json
        playerstats[teamOne] = team1Json

        with open('result.json', 'w') as fp:
            json.dump(playerstats, fp, indent=4)
        

def retreive_game_stat(game_id, access_token): #obtain a json file that stores all the stats Synergy has, obtained from a get reqeuest provided by Synergy
    url = f"http://basketball.synergysportstech.com/external/api/games/{game_id}/boxscores"
    gameStats = requests.get(url,headers={'Authorization': f'Bearer {access_token}'})
    boxJS = gameStats.json()["data"]
    with open(BOX_SCORE_JSON, 'w') as f:
        json.dump(boxJS, f, indent=4)
    printPlayerStats(boxJS)

def lineupStats(player1, player2, player3, player4, player5):
    with open('result.json') as json_data:
        playerStats = json.load(json_data)
        json_data.close()

    p1json = playerStats[player1]
    p2json = playerStats[player2]
    p3json = playerStats[player3]
    p4json = playerStats[player4]
    p5json = playerStats[player5]

    lineupStats = {}
    lineupStats['player1'] = player1
    lineupStats['player2'] = player2
    lineupStats['player3'] = player3
    lineupStats['player4'] = player4
    lineupStats['player5'] = player5

    lineupStats['teamORB%'] = (p1json['ORB%'] + p2json['ORB%'] + p3json['ORB%'] + p4json['ORB%'] + p5json['ORB%']) / 5
    lineupStats['teamDRB%'] = (p1json['DRB%'] + p2json['DRB%'] + p3json['DRB%'] + p4json['DRB%'] + p5json['DRB%']) / 5
    lineupStats['teamPPP'] = (p1json['ppp'] + p2json['ppp'] + p3json['ppp'] + p4json['ppp'] + p5json['ppp']) / 5
    lineupStats['teamORTG'] = (p1json['ORTG'] + p2json['ORTG'] + p3json['ORTG'] + p4json['ORTG'] + p5json['ORTG']) / 5
    lineupStats['teamTS%'] = (p1json['TrueShooting'] + p2json['TrueShooting'] + p3json['TrueShooting'] + p4json['TrueShooting'] + p5json['TrueShooting']) / 5
    lineupStats['teamEFG'] = (p1json['EFG'] + p2json['EFG'] + p3json['EFG'] + p4json['EFG'] + p5json['EFG']) / 5
    lineupStats['teamPER'] = (p1json['PER'] + p2json['PER'] + p3json['PER'] + p4json['PER'] + p5json['PER']) / 5

    with open('lineup.json', 'w') as fp:
            json.dump(lineupStats, fp, indent=4)

access_token = get_access_token(TOKEN_URL, CLIENT_ID, CLIENT_SECRET) # retreive access token for our new session
retreive_game_stat(GAME_ID, access_token)
lineupStats("Mark Sears", "Aden Holloway",  "Mouhamed Dioubate", "Grant Nelson", "Clifford Omoruyi")