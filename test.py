import json
import requests

requests.put("http://127.0.0.1:8000/rules/editUser/", files = {
    'imgs': open('1.jpg', 'rb')
})