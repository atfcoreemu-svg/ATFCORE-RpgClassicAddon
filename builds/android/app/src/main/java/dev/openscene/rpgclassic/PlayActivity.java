package dev.openscene.rpgclassic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import androidx.documentfile.provider.DocumentFile;
import org.easyrpg.player.player.EasyRpgPlayerActivity;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public final class PlayActivity extends Activity {
    private static final String ACTION = "dev.openscene.rpgclassic.action.PLAY";
    private static final String CALLER = "dev.openscene.player";
    private static final int PLAYER_REQUEST = 1;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) return;
        Intent request = getIntent();
        Uri uri = request.getData();
        if (!CALLER.equals(getCallingPackage()) || !ACTION.equals(request.getAction())
                || uri == null || !"content".equals(uri.getScheme()) || !DocumentsContract.isTreeUri(uri)) {
            fail("Solicitação de abertura inválida.");
            return;
        }
        try {
            DocumentFile root = DocumentFile.fromTreeUri(this, uri);
            if (root == null || !root.isDirectory() || !root.canRead()) {
                fail("O add-on não conseguiu ler a pasta selecionada.");
                return;
            }
            boolean database = false, mapTree = false;
            for (DocumentFile child : root.listFiles()) {
                String name = child.getName();
                if (name == null || !child.isFile()) continue;
                String lower = name.toLowerCase(Locale.ROOT);
                database |= lower.equals("rpg_rt.ldb");
                mapTree |= lower.equals("rpg_rt.lmt");
            }
            if (!database || !mapTree) {
                fail("Selecione uma pasta com RPG_RT.ldb e RPG_RT.lmt. O motor RGSS ainda não está incluído.");
                return;
            }
            String id = UUID.nameUUIDFromBytes(uri.toString().getBytes(StandardCharsets.UTF_8)).toString();
            File save = new File(getFilesDir(), "saves/" + id);
            if (!save.isDirectory() && !save.mkdirs()) {
                fail("Não foi possível preparar a pasta de saves.");
                return;
            }
            String log = new File(save, "easyrpg-player.log").getAbsolutePath();
            Intent play = new Intent(this, EasyRpgPlayerActivity.class);
            play.putExtra(EasyRpgPlayerActivity.TAG_COMMAND_LINE, new String[]{
                    "--project-path", root.getUri().toString(), "--save-path", save.getAbsolutePath(),
                    "--config-path", getFilesDir().getAbsolutePath(), "--log-file", log
            });
            play.putExtra(EasyRpgPlayerActivity.TAG_SAVE_PATH, save.getAbsolutePath());
            play.putExtra(EasyRpgPlayerActivity.TAG_LOG_FILE, log);
            play.putExtra(EasyRpgPlayerActivity.TAG_STANDALONE, true);
            startActivityForResult(play, PLAYER_REQUEST);
        } catch (RuntimeException error) {
            fail("Não foi possível abrir a pasta: " + error.getLocalizedMessage());
        }
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PLAYER_REQUEST) return;
        if (result == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        } else {
            String error = data == null ? null : data.getStringExtra("error");
            fail(error == null ? "O EasyRPG encerrou sem confirmar o fechamento. Consulte o log do jogo." : error);
        }
    }

    private void fail(String message) {
        setResult(RESULT_CANCELED, new Intent().putExtra("error", message));
        finish();
    }
}
