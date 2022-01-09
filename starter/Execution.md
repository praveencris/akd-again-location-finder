Q1. How did you generated sha1 debug signing certificate?
Ans:- I executed following command from jre/bin, where keytool file is provided by java.
Command: C:\Program Files\Android\Android Studio\jre\bin>keytool -list -v -alias androiddebugkey -keystore "C:\Users\pkchm\.android\debug.keystore"

