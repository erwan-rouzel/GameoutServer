# GameOutServer
Partie réseau pour game out.

# Procédure de déploiement

1) Créer une instance en autorisant les ports suivants en inbound / outbound :
9475 (TCP)
9476 (UDP)
9500 (UDP)

2) Installer la JVM (dernière version du JDK, a priori la 1.8) sur l'instance créée.

3) Dans IntelliJ, créer le fichier .jar qui contient l'ensemble du code compilé. Pour cela aller dans le menu "Build", puis faire "Build artifacts...". Cela ouvre un petit menu contextuel dans l'éditeur, sélectionner "Build" dans ce menu.

Cela crée un fichier .jar qui se trouve à cet endroit :

```bash
out/artifacts/GameoutServer/GameoutServer.jar
```

4) Il suffit alors de copier ce fichier sur le serveur où l'on souhaite déployer, du type :

```bash
scp out/artifacts/GameoutServer/GameoutServer.jar <login>@<ip_instance_aws>:/home/<login>/gameout/
```

5) Puis lancer le jar en se connectant au serveur de cette façon (le nohup sert à garder le serveur lancé même après déconnexion du serveur)  :

```bash
ssh <login>@<ip_instance_aws>
cd /home/<login>/gameout/
nohup java -jar GameoutServer.jar &
```

6) Pour suivre le log du serveur :

```bash
tail -f nohup.out
```

7) Il reste alors à changer l'IP côté Android en mettant la nouvelle IP de l'instance.
Et voilà en principe ça fonctionne !
