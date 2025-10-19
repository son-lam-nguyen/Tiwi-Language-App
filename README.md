# Tiwi Language Practice



A simple teacher and student language-practice app built with Java in Android Studio.

Teachers record reference pronunciations for sample sentences; students record their own versions and can lister to teacher version to compare.



User story: “As a student, I want to select a sentence, record my pronunciation, and compare it with the teacher’s recording, so I can practice and improve Tiwi language pronunciation.”





### ✨ Prototype scope



Roles: Teacher/Student (toggle on home screen)



Dataset: sample sentences from Tiwi Dictionary (assets/sentences.json)



Recordings: in-app audio capture (.m4a) saved to app local storage (Firebase later)



Persistence: a writable JSON mirror (files/sentences\_state.json) stores paths + metadata for recordings





### 🧠 Hypothesis



Hypothesis: Side-by-side access to teacher audio and a student’s own recording reduces confusion and speeds up pronunciation learning.



User stories (MVP):



Teacher records a reference for a sentence and saves it.



Student records their version and listen to Teacher version to compare.



Future stories:



Cloud sync via Firebase (Storage + Firestore)



Multiple student takes with waveform/score



Offline packs; export/share



Optional geotag/camera notes for field collection



### 📱 Screens



Main Screen: title + role toggle + list of sentences



Sentence detail for Teacher role: record/stop, play Teacher's Recording



Sentence detail for Student role: record/stop, play Teacher's Recording, play Your Recording (role-aware UI)



### How to run the code



1\. Clone the repo using SourceTree or Git: git clone https://hit226\_semester1\_2025-admin@bitbucket.org/hit238-tiwiapp2/hit238-tiwiapp2.bitbucket.io.git

2\. Open in Android Studio → Open an existing project → select this folder.

Let Gradle sync and accept any SDK prompts.

3\. Select a device (e.g., Pixel 6 / API 34 emulator)

4\. Click Run ▶ (configuration: app).

### 

### 🧭 References:



https://chatgpt.com/

https://codepen.io/ckutay/pen/zxvpmLN

https://www.geeksforgeeks.org/android/how-to-create-menu-folder-menu-file-in-android-studio/

https://developer.android.com/develop/ui/views/components/radiobutton

https://developer.android.com/develop/ui/views/components/menus

https://www.geeksforgeeks.org/android/audio-recorder-in-android-with-example/

https://developer.android.com/develop/connectivity/bluetooth/ble-audio/audio-recording

https://developer.android.com/reference/android/media/AudioRecord

https://developer.android.com/media/platform/av-capture

