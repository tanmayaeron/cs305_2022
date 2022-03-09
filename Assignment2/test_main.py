import main
from fastapi.testclient import TestClient

client = TestClient(main.app)

def test_add_face():
    assert True
    with open('resource/img.jpg','rb') as f:
        response = client.post("/add_face/",files={'file':f})
        assert response.status_code == 200
        assert response.json() == {"status":"OK","rowsInserted":1}

def test_search_faces():
    with open('resource/img.jpg','rb') as f:
        response = client.post("/search_faces/",files={'file':f},data={'k':1,'tolerance':0.6})
        expected = {
                        "status": "OK",
                        "body": {
                                  "matches": {
                                    "face1": [{
                                                "id": 1,
                                                "name": "img"
                                                }]
                                    }
                                }
                    }
        assert response.status_code == 200
        assert response.json()==expected

def test_get_face_info():
    response = client.post("/get_face_info/",data={'api_key':'123abcde','face_id':'1'})
    expected  = {
    "status": "OK",
    "info": {
        "Name": "img",
        "Version": "NA",
        "Date": "NA",
        "Location": "NA"
        }
    }
    assert response.status_code == 200
    assert response.json() == expected


def test_add_faces_in_bulk():
    with open('resource/check.zip','rb') as f:
        response = client.post("/add_faces_in_bulk/",files={'file':f})
        assert response.status_code == 200
        assert response.json() == {"status":"OK","rowsInserted":741}
