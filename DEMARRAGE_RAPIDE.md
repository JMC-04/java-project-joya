# ⚡ Démarrage Rapide - Bibliothèque Universitaire v2.0

## 🚀 Lancement en 3 Étapes

### 1️⃣ Compiler
```bash
.\compile.bat
```
ou
```powershell
cd c:\Users\user\Desktop\javaProject
$files = Get-ChildItem -Recurse -Filter "*.java" src/ | Select-Object -ExpandProperty FullName
javac -d . -encoding UTF-8 $files
```

### 2️⃣ Lancer
```bash
.\run.bat
```
ou
```powershell
java -cp . fr.univ.bibliotheque.Main
```

### 3️⃣ Se Connecter
- **Admin** : `admin` / `admin`
- **Étudiant** : `alice` / `alice123` (ou autres dans universite.xml)

---

## 🎯 Que Faire Ensuite ?

### En tant qu'Admin
1. **Onglet "Gestion des Médias"**
   - Cliquer "Ajouter Média"
   - Remplir le formulaire (type, titre, auteur, etc.)
   - Sélectionner des matières
   - Valider

2. **Onglet "Statistiques & Exports"**
   - Choisir "Par spécialité" ou "Par matière"
   - Sélectionner une cible
   - Générer le rapport
   - Exporter en CSV ou XML si souhaité

3. **Créer des entités**
   - Nouvelle spécialité
   - Nouvelle matière
   - Nouvel étudiant

4. **Sauvegarder** avant de quitter !

### En tant qu'Étudiant
1. **Rechercher** un média par titre
2. **Appliquer des filtres** (auteur, spécialité, etc.)
3. **Sélectionner** un média dans la table
4. **Consulter** le média (compteur d'accès++)

---

## 📊 Structure du Projet

```
39 fichiers Java organisés en 8 packages :

model/          → 7 classes (Media, Document, Etudiant...)
view/           → 9 classes (GUI, Dialogs + contrôleurs intégrés)
service/        → 1 classe  (Catalog)
repository/     → 2 classes (EtudiantRepo, MediaRepo)
strategy/filter → 8 classes (Filtres de médias)
strategy/export → 3 classes (CSV, XML)
strategy/report → 3 classes (Stats)
observer/       → 3 classes (Notifications)
exception/      → 2 classes (Erreurs métier)
```

**Architecture** : MVC simplifié avec contrôleurs intégrés dans les vues

---

## 🔤 Naming Convention

**100% snake_case implémenté !**

```java
// Variables et méthodes
private int compteur_acces;
public String get_titre() { ... }
public void set_auteur(String auteur) { ... }
private void rechercher_par_titre(String titre) { ... }

// Classes (PascalCase)
public class MediaFormDialog { ... }

// Constantes (UPPER_SNAKE_CASE)
private static final String DEFAULT_XML_FILE = "data/universite.xml";
```

---

## 🎨 Fonctionnalités Clés

### ✨ Toutes Implémentées

| Feature | Description | Status |
|---------|-------------|--------|
| 🔍 Recherche ID | Recherche exacte par identifiant | ✅ |
| 🔍 Recherche titre | Recherche partielle | ✅ |
| 🎯 Filtres | Auteur, spécialité, type, année | ✅ |
| ➕ CRUD Médias | Ajouter, modifier, supprimer | ✅ |
| 👥 CRUD Entités | Spécialités, matières, étudiants | ✅ |
| 📊 Statistiques | Top médias par matière/spécialité | ✅ |
| 💾 Export CSV | Rapports en format CSV | ✅ |
| 💾 Export XML | Rapports en format XML | ✅ |
| 🔔 Notifications | Observer pattern (console) | ✅ |
| 💾 Persistance | XML + binaire | ✅ |

---

## 🛠️ Dépannage Rapide

### Problème : Erreur au démarrage
```
ERREUR : Classe invalide lors du chargement
```
**Solution** :
```bash
Remove-Item data\medias.ser
```
L'application redémarrera avec un catalogue vide.

### Problème : Compilation échoue
```
error: cannot find symbol
```
**Solution** :
```bash
# Nettoyer et recompiler
Remove-Item -Recurse fr/
.\compile.bat
```

### Problème : Catalogue vide
```
ATTENTION : Échec du chargement des étudiants
```
**Solution** : Vérifier que `data/universite.xml` existe

---

## 💡 Conseils d'Utilisation

### Pour Tester Rapidement
1. Lancer en mode Admin
2. Créer quelques médias de test
3. Assigner des matières
4. Générer des statistiques
5. Tester les exports
6. Sauvegarder
7. Relancer et vérifier la persistance

### Pour le Développement
- Toutes les classes utilisent des noms explicites
- La structure MVC facilite l'ajout de features
- Les interfaces Strategy permettent l'extension
- Pas besoin de toucher au code existant pour ajouter :
  - Un nouveau type de média
  - Un nouveau filtre
  - Un nouveau format d'export
  - Un nouveau type de rapport

---

## 📞 Informations

**Version** : 2.0  
**Date** : Janvier 2026  
**Auteur** : Bibliothèque Universitaire  
**Langage** : Java (java.*, javax.*)  
**UI** : Swing  
**Statut** : ✅ Production Ready  

---

## 🎊 Conclusion

Le projet a été **complètement refactorisé** selon les spécifications :

✅ Naming 100% snake_case  
✅ Structure MVC professionnelle  
✅ UI Swing claire et accessible  
✅ Fichiers inutiles supprimés  
✅ Fonctionnement préservé à 100%  

**Le projet est prêt à l'emploi !** 🚀

---

*Pour plus de détails, consultez README.md et REFACTORING_COMPLET.md*
