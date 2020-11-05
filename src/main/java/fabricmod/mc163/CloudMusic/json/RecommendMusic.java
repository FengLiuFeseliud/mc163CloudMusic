package fabricmod.mc163.CloudMusic.json;

import java.util.List;

public class RecommendMusic {
    String code;
    private List<Musiclist> musiclist;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Musiclist> getMusiclist() {
        return musiclist;
    }

    public void setMusiclist(List<Musiclist> musiclist) {
        this.musiclist = musiclist;
    }

    public class Musiclist {
        private String name;
        private String copywriter;
        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCopywriter() {
            return copywriter;
        }

        public void setCopywriter(String copywriter) {
            this.copywriter = copywriter;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
