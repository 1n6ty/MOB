import json, base64, hashlib, time

sessionTime = 3600

def isCorruptedToken(token: str, key: int) -> bool:
    data_str, hash_str = token.split('.')
    data = json.loads(base64.b64decode(bytes.fromhex(data_str)))
    hash_token = hashlib.sha256(str(key).encode('utf-8') + json.dumps(data).encode('utf-8')).hexdigest()
    if hash_token == hash_str:
        return False
    else:
        return True

def getDataFromToken(token: str) -> dict: 
    data_str, hash_str = token.split('.')
    return json.loads(base64.b64decode(bytes.fromhex(data_str))) 

def createSessionToken(data: dict, key: int) -> str:
    bytes_str = json.dumps(data).encode('utf-8')
    data_token = base64.b64encode(bytes_str).hex()
    hash_token = hashlib.sha256(str(key).encode('utf-8') + bytes_str).hexdigest()
    return data_token + '.' + hash_token

def sessionTimeExpired(timeU: int) -> bool:
    if int(time.time()) < timeU:
        return False
    return True

def removeCommas(s: str):
    if s[0] == '\"' or s[0] == '\'': s = s[1: ]
    if s[-1] == '\"' or s[-1] == '\'': s = s[: -1]
    return s