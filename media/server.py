__author__ = 'lundh'

import flask
app = flask.Flask(__name__)

@app.route("/")
def hello():
    return "Media server started."

@app.route("/media/<path:path>")
def images(path):
    #flask.generate_img(path)
    fullpath = "/home/pi/www/media/" + path
    resp = flask.make_response(open(fullpath).read())
    resp.content_type = "image/jpeg"
    return resp

#
# @app.route('/uploads/<path:filename>')
# def download_file(filename):
#     return send_from_directory(app.config["MEDIA_FOLDER"], filename, as_attachment=True)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8080, debug=True)