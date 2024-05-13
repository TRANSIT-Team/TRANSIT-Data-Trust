# Relocate Container from Host to Host

1. Auf Host verbinden
2. In Ordner für Backup wechseln

```
 cd /var/lib/docker/volumes/
````

3. Backup erstellen (.tar)

```
 tar -vcf backup_wordpress-mysql.tar -C /var/lib/docker/volumes/transit-wordpress_transit-mysql-data-volume_3c09e/_data/ .
````

4. Backup lokal in aktuellen Ordner kopieren

Im neuen CMD-Fenster
```
scp root@172.18.178.33:/backup-wordpress-wordpress-data.tar backup-wordpress-wordpress-data.tar 
```

1. (ggf. Docker Compose anpassen) neuen Container starten und danach stoppen
2. Ort für Backupwiederherstellung finden
3. Backup nach neuen Host kopieren
   
```
scp backup-wordpress-wordpress-data.tar root@172.18.178.55:/backup-wordpress-wordpress-data.tar 
```
8. Ordner vom neuen Container leeren (ex. _data)
9. Backup nach neuen Container im geleerten Ordner entpacken

```
tar -xvf backup_wordpress-mysql.tar -C /var/lib/docker/volumes/transit-wordpress-1_transit-mysql-data-volume-1_f6b94/_data/
```


10.  Container starten
11.  Prüfen ob Container läuft und erreichbar

Über externe IP (links im Rancher) oder unter Container->Networking die zweite Search-Domain

12. tar und überflüssige Dateien löschen (zb. auf alten Host löschen)
13. Jenkins anpassen