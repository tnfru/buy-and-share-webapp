Copy the pre-push file into the .git/hooks/ directory
Run chmod +x pre-push
Copy the pre-push.d directory into .git/hooks/
Run chmod +x on every file in it
Should work now. If it doesn't maybe you didn't load the updated Style config Google4Spaces?

if you get contextLoad test failed run:
./gradlew clean
this is due to refactoring

