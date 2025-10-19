package com.example.tiwilanguageapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class PhraseBookActivity extends AppCompatActivity {

    private PhrasebookAdapter adapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phrase_book);
        setupToolbar();
        setupRecyclerView();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(R.string.phrasebook_title);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                Drawable nav = toolbar.getNavigationIcon();
                if (nav != null) {
                    Drawable wrapped = DrawableCompat.wrap(nav);
                    DrawableCompat.setTint(wrapped, Color.WHITE);
                    toolbar.setNavigationIcon(wrapped);
                }
            }
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvPhrasebook);
        if (recyclerView == null) {
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhrasebookAdapter(buildPhraseEntries(), this::playPhrase);
        recyclerView.setAdapter(adapter);

    }

    private List<PhraseEntry> buildPhraseEntries() {
        return Arrays.asList(
                new PhraseEntry("What did she do?", "Awungana jiyima (female)?", R.raw.phrase_greeting),
                new PhraseEntry("How are you my friends?", "Awungana mamanta", R.raw.phrase_greeting),
                new PhraseEntry("How are you, my friend?", "Awungana nginja mantani (male)/ mantanga (female)?", R.raw.phrase_greeting),
                new PhraseEntry("What did they do?", "Awungana pirima (group)?", R.raw.phrase_greeting),
                new PhraseEntry("What did he do?", "Awungana yima (male)?", R.raw.phrase_greeting),
                new PhraseEntry("What’s in your heart?", "Kamini apalamiya kangi nginjila ruwuti?", R.raw.phrase_greeting),
                new PhraseEntry("What is this word/story that you could do to help yourself?", "Kamini awarra naki ngirramini ngini nginja wiyi aminyuwani?", R.raw.phrase_greeting),
                new PhraseEntry("What is this word/story that you could do which will help you feel better?", "Kamini awarra ngirramini ngini wiyi aminyuwani?", R.raw.phrase_greeting),
                new PhraseEntry("Why is he crying?", "Pilikama ngarra pirlinkiti awujingimi (male)?", R.raw.phrase_greeting),
                new PhraseEntry("Why are you not happy?", "Pilikama nginja karluwu kukunari?", R.raw.phrase_greeting),
                new PhraseEntry("Why is she crying?", "Pilkama nyirra pirlinkiti ampujingimi (female)?", R.raw.phrase_greeting),
                new PhraseEntry("Why are they crying?", "Pilikama pirlinkiti wujingimi (group)?", R.raw.phrase_greeting),
                new PhraseEntry("Where is the pain?", "Maka jana awurrumi?", R.raw.phrase_greeting),
                new PhraseEntry("Where do you feel the pain?", "Maka jana awurrumi?", R.raw.phrase_greeting),
                new PhraseEntry("Where is your hearing going?", "Maka minyawunga ngimpangirri/purrakuninga/mikajanga?", R.raw.phrase_greeting),
                new PhraseEntry("Where is your head/thinking going (not concentrating)?", "Maka nginja punyipunyi kawunaga arimi", R.raw.phrase_greeting),
                new PhraseEntry("She has good ears/is a good listener", "Mikajanga pumpuka (female)", R.raw.phrase_greeting),
                new PhraseEntry("He has good ears/is a good listener", "Mikajanga pupuni (male)", R.raw.phrase_greeting),
                new PhraseEntry("She has good ears", "Mikajanga/Purrakuninga (ears) pumpuka (female) wuni", R.raw.phrase_greeting),
                new PhraseEntry("He has good ears", "Mikajanga/Purrakuninga (ears) pupuni (male) wuni", R.raw.phrase_greeting),
                new PhraseEntry("Let’s talk together", "Murrajiyarrapirri", R.raw.phrase_greeting),
                new PhraseEntry("Let’s talk about what is in your heart/mind", "Murrajiyarrapirri ngini apalamiya kangijila ruwuti/punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("You and I walking together/side by side", "Muwiyati murrangurlimayi", R.raw.phrase_greeting),
                new PhraseEntry("Listen/hear", "Ngaripirtangaya", R.raw.phrase_greeting),
                new PhraseEntry("Leaving worries/bad stories behind. Go forward, follow the good road.", "Ngawurramwariyi ngini kutakamini jirti ngirramini ngintirikimani. Ngapapurukaga angi pupuka jarrumwaka.", R.raw.phrase_greeting),
                new PhraseEntry("Care for each other", "Ngawurrayamangajirri", R.raw.phrase_greeting),
                new PhraseEntry("Look after ourselves", "Ngawurrayamangamiya", R.raw.phrase_greeting),
                new PhraseEntry("If you’ve got bad feeling, I am here to talk with you", "Ngini jirti apalamiya kangijila purnikapa api murrajiyarrapirri", R.raw.phrase_greeting),
                new PhraseEntry("Who are you related to?/Who is your family?", "Nginja kuwapi ngirimipi?", R.raw.phrase_greeting),
                new PhraseEntry("Did you sleep well last night?", "Nginja jimajirripi pupuni japinana?", R.raw.phrase_greeting),
                new PhraseEntry("Are you hungry?", "Nginja paruwani?", R.raw.phrase_greeting),
                new PhraseEntry("Are you ok?", "Nginja pupukana? (female) or Nginja pupuka? (female)", R.raw.phrase_greeting),
                new PhraseEntry("Are you ok?", "Nginja pupunana? (male) or Nginja pupuni? (male)", R.raw.phrase_greeting),
                new PhraseEntry("I am here if you need to talk", "Ngiya awungarra ngini murrajiyarrapirri ngini kutakamini apalamiya kangijila", R.raw.phrase_greeting),
                new PhraseEntry("I am here if you need to talk about what is in your heart and mind", "Ngiya awungarra ngini murrajiyarrapirri ngini kutakamini apalamiya kangijila ruwuti amintiya punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("I didn’t sleep well last night", "Ngiya karluwu pupuni ngirimajirripi japini", R.raw.phrase_greeting),
                new PhraseEntry("I listen to a story without getting involved/ I observe their story", "Ngiya ngiripirtangaya ngini nginja ngimpirimirra", R.raw.phrase_greeting),
                new PhraseEntry("I will help you", "Ngiya wiyi ngiminyiwani", R.raw.phrase_greeting),
                new PhraseEntry("I will come to see you", "Ngiya wiyi nyiminjakurluwunyi", R.raw.phrase_greeting),
                new PhraseEntry("I will come to see you later", "Ngiya wiyi parlinginari nyiminjakurluwunyi", R.raw.phrase_greeting),
                new PhraseEntry("Why is he angry?", "Pilikama ngarra arimikimirtiyarri (male)?", R.raw.phrase_greeting),
                new PhraseEntry("Why do you feel shame?", "Pilikama nginja arliranga?", R.raw.phrase_greeting),
                new PhraseEntry("Why are you feeling sad/unhappy?", "Pilikama nginja mirliga?", R.raw.phrase_greeting),
                new PhraseEntry("Why are you sleeping a lot?", "Pilikama nginja yunukurni nyimpirimajirripi?", R.raw.phrase_greeting),
                new PhraseEntry("Why do all of you feel shame?", "Pilikama nuwa arliranga (group)?", R.raw.phrase_greeting),
                new PhraseEntry("Why are they angry?", "Pilikama nuwa ngimpirimikimirtiyarri (group)?", R.raw.phrase_greeting),
                new PhraseEntry("Why is she angry?", "Pilikama nyirra ampirimikimirtiyarri (female)?", R.raw.phrase_greeting),
                new PhraseEntry("Why are you arguing?", "Pilikama nuwa ngimpiriwayatipi?", R.raw.phrase_greeting),
                new PhraseEntry("Why are you both arguing?", "Pilikama nuwa ngupujinguwayatipi?", R.raw.phrase_greeting),
                new PhraseEntry("Good afternoon", "Pupuni awurlanari", R.raw.phrase_greeting),
                new PhraseEntry("Good night", "Pupuni japinamirni", R.raw.phrase_greeting),
                new PhraseEntry("Good Morning", "Pupuni japinari", R.raw.phrase_greeting),
                new PhraseEntry("Look after yourself", "Tayamangamiya", R.raw.phrase_greeting),
                new PhraseEntry("We will talk together later", "Wiyi parlinginari nimarra murramikimi", R.raw.phrase_greeting),
                new PhraseEntry("Later on, we will talk together", "Wiyi parlinginari nimarra murumi", R.raw.phrase_greeting),
                new PhraseEntry("Feeling or thinking she is good/proud/“flash”", "Ampangirrajamiya (female)", R.raw.phrase_greeting),
                new PhraseEntry("Frightened/scared", "Ampirimakirri (female)", R.raw.phrase_greeting),
                new PhraseEntry("Frightened/scared", "Arimakirri (male)", R.raw.phrase_greeting),
                new PhraseEntry("Shame/Shy (main meanings). Can also mean: “Sorry” for others, Unhappy/Worried", "Arliranga", R.raw.phrase_greeting),
                new PhraseEntry("Feeling or thinking he is good/proud/“flash”", "Arrungiramiya (male)", R.raw.phrase_greeting),
                new PhraseEntry("Stressing. Can also mean: Confused/paranoid/not coping", "Awuntingirraga", R.raw.phrase_greeting),
                new PhraseEntry("Hearing voices in their ears telling them what to do", "Awuta mamakuwi nimarra wujingimi kangi wuta minyawunga", R.raw.phrase_greeting),
                new PhraseEntry("Hearing voices in their head telling them what to do", "Awuta mamakuwi nimarra wujingimi kangi wuta punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("Feeling weak or tired", "Janawurti", R.raw.phrase_greeting),
                new PhraseEntry("She got a shock", "Jikitarriji/jipakilimigi angatawa yiminga (female)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling strong (body and mind)", "Jikurturumi (female)", R.raw.phrase_greeting),
                new PhraseEntry("“No good”. Can also mean: Annoyed/sick", "Jirti", R.raw.phrase_greeting),
                new PhraseEntry("Feeling “no good” in your body. Can also mean: Not feeling yourself/Sad inside", "Jirti yilipiga", R.raw.phrase_greeting),
                new PhraseEntry("Whispering to himself", "Jukutingini (male)", R.raw.phrase_greeting),
                new PhraseEntry("Whispering to herself", "Jukutinga (female)", R.raw.phrase_greeting),
                new PhraseEntry("Whispering to themselves", "Jukutingimpi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Sweating", "Kalampara", R.raw.phrase_greeting),
                new PhraseEntry("She ran away", "Kali jimakirringimi (female)", R.raw.phrase_greeting),
                new PhraseEntry("She ran away from her bad story (problems/worries)", "Kali jimakirringimi pili jirti ngirramini (female)", R.raw.phrase_greeting),
                new PhraseEntry("They ran away", "Kali wurimakirrimi (more than one)", R.raw.phrase_greeting),
                new PhraseEntry("He ran away", "Kali yimakirringimi (male)", R.raw.phrase_greeting),
                new PhraseEntry("He ran away from his bad story (problems/worries)", "Kali yimakirringimi pili jirti ngirramini (male)", R.raw.phrase_greeting),
                new PhraseEntry("People who have worries/“stressing”/not coping", "Kapi awuntingirraga", R.raw.phrase_greeting),
                new PhraseEntry("Our friends who have worries/“stressing”/not coping", "Kapi ngawa mamanta awuntingirraga", R.raw.phrase_greeting),
                new PhraseEntry("People who have worries/“stressing”/not coping", "Kapi wuta awuntingirraga", R.raw.phrase_greeting),
                new PhraseEntry("Not happy", "Karluwu kukunari", R.raw.phrase_greeting),
                new PhraseEntry("I didn’t sleep well", "Karluwu pupuni ngirimajirripi", R.raw.phrase_greeting),
                new PhraseEntry("I have no friends", "Karrikuwapi ngiya mamanta", R.raw.phrase_greeting),
                new PhraseEntry("I have no family", "Karrikuwapi ngiya ngirimipi", R.raw.phrase_greeting),
                new PhraseEntry("Nobody loves me/cares for me", "Karrikuwapi wurtimarti ngiya", R.raw.phrase_greeting),
                new PhraseEntry("Happy, Relaxed/calm, Proud", "Kukunari/Miringayi", R.raw.phrase_greeting),
                new PhraseEntry("Somebody is talking, but it is all in my head/I can hear people talking inside my head (auditory hallucinations)", "Kutakuwapi nimarra wujingimi kapi ngiya punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("He has lost touch with reality/his mind is somewhere else", "Kutamaka ngarra punyipunyi arimuwu (male)", R.raw.phrase_greeting),
                new PhraseEntry("She has lost touch with reality/her mind is somewhere else", "Kutamaka nyirra punyipunyi ampirimuwu (female)", R.raw.phrase_greeting),
                new PhraseEntry("They lose touch with reality/their mind is somewhere else", "Kutamaka wuta punyipunyi arimuwu (group)", R.raw.phrase_greeting),
                new PhraseEntry("“Cheeky drugs”/”hot drugs”/Synthetic drugs (e.g. Synthetic marijuana, ice, methamphetamine)", "Majaripika", R.raw.phrase_greeting),
                new PhraseEntry("Hearing voices", "Mamankuwi wujirrangiraga", R.raw.phrase_greeting),
                new PhraseEntry("Scared", "Mankirringini/ arimakirri (male)", R.raw.phrase_greeting),
                new PhraseEntry("Scared", "Mankirrika / ampirimakirri (female)", R.raw.phrase_greeting),
                new PhraseEntry("Scared", "Mankirringimpi / wurumakirri (group)", R.raw.phrase_greeting),
                new PhraseEntry("Jealous", "Martupungari", R.raw.phrase_greeting),
                new PhraseEntry("Feeling good", "Mijuwalini/pupuni (male)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling good", "Mijuwalinga/pupuka (female)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling good", "Mijuwaluwi/Papuranjuwi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Unhappy", "Mirliga", R.raw.phrase_greeting),
                new PhraseEntry("Beer/“Grog”/Alcohol. Also means: Saltwater", "Mirripaka", R.raw.phrase_greeting),
                new PhraseEntry("Don’t get angry/aggressive", "Ngajiti ngawujakirimitiyarri", R.raw.phrase_greeting),
                new PhraseEntry("Sweating", "Ngarikilini", R.raw.phrase_greeting),
                new PhraseEntry("He is deadly/smart/clever", "Ngarra arungurramiya (male)", R.raw.phrase_greeting),
                new PhraseEntry("He is surprised", "Ngarra arimingarlingi (male)", R.raw.phrase_greeting),
                new PhraseEntry("He took his life away (suicide)", "Ngarra yipirnamiya (male)", R.raw.phrase_greeting),
                new PhraseEntry("Teaching people/reminding ourselves of the right story, to keep on track (positive thinking)", "Ngawa ngintimatapiliga nginingawula ngirramini", R.raw.phrase_greeting),
                new PhraseEntry("Our family see things that are not there when they are sick", "Ngawa mamanta wupakurluwunyi mamakuwi karri wuta jana", R.raw.phrase_greeting),
                new PhraseEntry("I am not hungry", "Ngiya karluwu paruwani", R.raw.phrase_greeting),
                new PhraseEntry("I am not well", "Ngiya karluwu pupuka (female)", R.raw.phrase_greeting),
                new PhraseEntry("I am not well", "Ngiya karluwu pupuni (male)", R.raw.phrase_greeting),
                new PhraseEntry("I am on my own/alone", "Ngiya wangantamiya", R.raw.phrase_greeting),
                new PhraseEntry("I am hot. Can also mean: I am angry", "Ngiya yikwanari", R.raw.phrase_greeting),
                new PhraseEntry("She is talking to herself", "Nimarra ampirimi nyitamiya (female)", R.raw.phrase_greeting),
                new PhraseEntry("He is talking to himself", "Nimarra arimi ngatamiya (male)", R.raw.phrase_greeting),
                new PhraseEntry("They talk to themselves", "Nimarra wurimi wutilamiya (group)", R.raw.phrase_greeting),
                new PhraseEntry("They are talking to themselves", "Nimarra wujingimi wutamiya (group)", R.raw.phrase_greeting),
                new PhraseEntry("She is surprised", "Nyirra ampirimarlingi (female)", R.raw.phrase_greeting),
                new PhraseEntry("She took her life away (suicide)", "Nyirra jipirnamiya (female)", R.raw.phrase_greeting),
                new PhraseEntry("I am “sorry” (I acknowledge your or my grief)", "Nuwa nguriyi", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/uneasy. Heart beating fast, a problem/bad news is coming", "Palipali ampirimi angilawa yiminga", R.raw.phrase_greeting),
                new PhraseEntry("From the past, they still carry that bad feeling inside them (trauma)", "Parlingarri ngini jirti ngirramini purruwunani api karluwu kiyija arnturuka", R.raw.phrase_greeting),
                new PhraseEntry("Big eyes (staring)", "Pijarama (female)", R.raw.phrase_greeting),
                new PhraseEntry("Big eyes (staring)", "Pijaramini (male)", R.raw.phrase_greeting),
                new PhraseEntry("Big eyes (staring)", "Pijaramuwi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Why did he look at me? (suspicious)", "Pilikama ngarra yimintakurluwunyi (male)?", R.raw.phrase_greeting),
                new PhraseEntry("Why are they looking at me? (suspicious)", "Pilikama nuwa ngintimantakurluwunyi (group)?", R.raw.phrase_greeting),
                new PhraseEntry("Why did she look at me? (suspicious)", "Pilikama nyirra jimintakurluwunyi (female)?", R.raw.phrase_greeting),
                new PhraseEntry("Talking all jumbled up (talking but not making sense)", "Pirlamarri ampangiraga (female)", R.raw.phrase_greeting),
                new PhraseEntry("Talking all jumbled up (talking but not making sense)", "Pirlamarri apangiraga (male)", R.raw.phrase_greeting),
                new PhraseEntry("Talking all jumbled up (talking but not making sense)", "Pirlamarri wupangiraga kapi naki (group)", R.raw.phrase_greeting),
                new PhraseEntry("These people are talking all jumbled up (these people are talking but not making sense)", "Pirlamarri wupangiraga ngawa mamanta (group)", R.raw.phrase_greeting),
                new PhraseEntry("Crying", "Pirlinkiti awujingimi (male)", R.raw.phrase_greeting),
                new PhraseEntry("Crying", "Pirlinkiti ampujingimi (female)", R.raw.phrase_greeting),
                new PhraseEntry("Crying", "Pirlinkiti wujingimi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling weak or tired", "Pirringawuni", R.raw.phrase_greeting),
                new PhraseEntry("I am being loved", "Puranji wurumuwu ngawa", R.raw.phrase_greeting),
                new PhraseEntry("They love me", "Puranji wurumuwu ngiya", R.raw.phrase_greeting),
                new PhraseEntry("Feeling strong as a group", "Purukurturumi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Nervous, shaking (anxiety)", "Purlingiya", R.raw.phrase_greeting),
                new PhraseEntry("I am shaky", "Purlingiya ngirimi", R.raw.phrase_greeting),
                new PhraseEntry("I am shaky inside my stomach (nausea)", "Purlingiya ngirimi kapi ngiya pitipita", R.raw.phrase_greeting),
                new PhraseEntry("Pain in the head/headache", "Pungintaga jana", R.raw.phrase_greeting),
                new PhraseEntry("I slept well last night/good sleep", "Pupuni ngirimajirripi japini", R.raw.phrase_greeting),
                new PhraseEntry("Feel “sorry” for that family/we feel “sorry” for them (grief)", "Putuputuwu ngawuntakirayi ngawa mamanta", R.raw.phrase_greeting),
                new PhraseEntry("We grieve for ourselves/ Feeling “sorry” for ourselves", "Putuputuwu ngawurrakirayamiya", R.raw.phrase_greeting),
                new PhraseEntry("He is laughing to himself", "Pwakayini kapi ngatamiya (male)", R.raw.phrase_greeting),
                new PhraseEntry("She is laughing to herself", "Pwakayini kapi nyitamiya (female)", R.raw.phrase_greeting),
                new PhraseEntry("They are laughing to themselves", "Pwakayini wurimi wutilamiya (group)", R.raw.phrase_greeting),
                new PhraseEntry("Heart beating fast", "Ruwuti ampilampa", R.raw.phrase_greeting),
                new PhraseEntry("Everyone is talking in my head", "Tayikuwapi nimarra wujingimi kapi ngiya punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("I am going back to my homeland because I am feeling sick (longing for country)", "Waya nguwujakupawurli kapi ngiya timani pili ngiya waya jana yiminipirni.", R.raw.phrase_greeting),
                new PhraseEntry("Hearing voices", "Wujirrangiraga kangawa purrakuninga", R.raw.phrase_greeting),
                new PhraseEntry("Seeing spirits/seeing things that are not there (visual hallucinations)", "Wupakurluwunyi yimanka", R.raw.phrase_greeting),
                new PhraseEntry("They are feeling or thinking they are good/proud/“flash”", "Wupangirrajamiya (group)", R.raw.phrase_greeting),
                new PhraseEntry("“Gunja”/cannabis/marijuana", "Wupunga", R.raw.phrase_greeting),
                new PhraseEntry("Frightened/scared", "Wurumakirri (group)", R.raw.phrase_greeting),
                new PhraseEntry("They are surprised", "Wuta wurumarlingi (group)", R.raw.phrase_greeting),
                new PhraseEntry("They are talking to themselves", "Wutamiya nimarra wujingimi (group)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure", "Yartari", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure with shortness of breath", "Yartari ampirimarrimi angi nyitawa yiminga (female)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure with shortness of breath", "Yartari arimarrimi angatawa yiminga (male)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure with head (thoughts) racing", "Yartari ngarimajigi nginingawula punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure with heart beating fast", "Yartari wurumarrimi angiwutawa yiminga", R.raw.phrase_greeting),
                new PhraseEntry("Feeling anxious/unsure and not thinking straight (confused)", "Yartari wurumajigi nginiwutawa punyipunyi", R.raw.phrase_greeting),
                new PhraseEntry("He got a shock", "Yikitarriji/yipakilimigi angatawa yiminga (male)", R.raw.phrase_greeting),
                new PhraseEntry("Feeling strong. NB: can represent body and mind", "Yikurturumi (male)", R.raw.phrase_greeting),
                new PhraseEntry("Goosebumps", "Yilintiraga", R.raw.phrase_greeting),
                new PhraseEntry("Heartbeat/Pulse. Can also mean: Totem, Craving for bush tucker", "Yiminga", R.raw.phrase_greeting),
                new PhraseEntry("Tight muscles (feeling tense)", "Yinjinga juwurumi", R.raw.phrase_greeting),
                new PhraseEntry("Sometimes I forget", "Yingampini nyimpangirliparra", R.raw.phrase_greeting),
                new PhraseEntry("Lots of beer (Intoxicated/drunk)", "Yingarti mirripaka", R.raw.phrase_greeting),
                new PhraseEntry("There are lots of voices/people talking around us", "Yingarti pujinga nimarra wujingimi kangawula", R.raw.phrase_greeting),
                new PhraseEntry("Lots of “gunja” (Intoxicated/”stoned”/feeling the effects of cannabis)", "Yingarti wupunga", R.raw.phrase_greeting)
        );
    }

    private void playPhrase(PhraseEntry entry) {
        if (entry.audioResId == null) {
            Toast.makeText(this, R.string.phrasebook_audio_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();

        mediaPlayer = MediaPlayer.create(this, entry.audioResId);
        if (mediaPlayer == null) {
            Toast.makeText(this, R.string.phrasebook_audio_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer.setOnCompletionListener(mp -> releasePlayer());
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            releasePlayer();
            return true;
        });
        mediaPlayer.start();
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }
}
