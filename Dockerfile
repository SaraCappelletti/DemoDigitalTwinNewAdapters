# Usa un'immagine di base di Java
FROM openjdk:11-jre

# Crea una directory /app all'interno del contenitore
RUN mkdir /app

# Copia il tuo file ZIP nell'immagine
COPY ./app/build/distributions/app.zip /app

# Sposta il file ZIP nella directory /app
WORKDIR /app
RUN unzip app.zip -d app

EXPOSE 1883
#L'istruzione EXPOSE <port> informa Docker che il container sarà in ascolto sulle porte specificate;
#documenta che le porte indicate sono disponibili alla pubblicazione, ma non le pubblica.
#Solo con il comando docker run -p 8080:8080 applicazione_prova mappiamo effettivamente la porta 8080 del container
#sulla porta 8080 dell'host (il primo numero è riferito all'host, mentre il secondo indica la porta del container).
#Rimuovendo l'istruzione EXPOSE dal Dockerfile è comunque possibile esporre le porte desiderate senza incorrere in errori.

# Specifica il comando di avvio dell'applicazione
CMD ["sh", "./app/app/bin/app"]
