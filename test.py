import requests

data = requests.get('http://127.0.0.1:8000/rules/getPost', {
    'token': '65794a705a4349364944457349434a7362324e6864476c76626c39705a43493649434978496e303d.68fe852e9281f46d7777da4fb4d72a7a83dce8b09214db9772eaee69aeae2714',
    'post_id': '1',
})

print(data.json())