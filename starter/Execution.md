Q1. How did you generated sha1 debug signing certificate?
Ans:- I executed following command from jre/bin, where keytool file is provided by java.
Command: C:\Program Files\Android\Android Studio\jre\bin>keytool -list -v -alias androiddebugkey -keystore "C:\Users\pkchm\.android\debug.keystore"

Q2: How to ignore google map api key to be added to github repository?
Ans: Steps:-
     1. Add following lines to .gitignore file
        /app/src/release/res/values/google_maps_api.xml
        /app/src/debug/res/values/google_maps_api.xml
     2. Run following commands in order
         a. git rm -r --cached .
         b. git add .
         c. git commit -m ".gitignore fixed"
     3. Reference : https://bytefreaks.net/programming-2/my-gitignore-file-is-ignored-by-git-and-it-does-not-work
