import urllib.request
from http.client import IncompleteRead

try:
    response = urllib.request.urlopen('http://localhost:8080/public/vehicles')
    data = response.read()
except IncompleteRead as e:
    data = e.partial

with open('output4.html', 'wb') as f:
    f.write(data)
