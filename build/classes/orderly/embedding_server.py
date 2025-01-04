# pip install flask sentence-transformers
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer

# Initialize the Flask app
app = Flask(__name__)

# Load the pre-trained sentence transformer model
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

@app.route('/get_embeddings', methods=['POST'])
def get_embeddings():
    # Get the input data from the JSON payload
    data = request.get_json()

    if 'sentences' not in data:
        return jsonify({'error': 'No sentences provided'}), 400

    sentences = data['sentences']

    # Generate embeddings for the input sentences
    embeddings = model.encode(sentences)

    # Return the embeddings as JSON
    return jsonify({'embeddings': embeddings.tolist()})


if __name__ == '__main__':
    # Run the Flask app (host=0.0.0.0 makes it publicly accessible)
    app.run(host='0.0.0.0', port=5000)
