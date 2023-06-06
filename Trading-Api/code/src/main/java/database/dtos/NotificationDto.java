package database.dtos;

import jakarta.persistence.*;
import utils.messageRelated.NotificationOpcode;

@Entity
@Table(name = "notifications")
public class NotificationDto{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "userId"), referencedColumnName = "id")
    private MemberDto memberDto;
    private String content;
    private int opcode;

    public NotificationDto(){
    }
    public NotificationDto(MemberDto memberDto, String content, int opcode){
        this.memberDto = memberDto;
        this.content = content;
        this.opcode = opcode;

    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MemberDto getMemberDto() {
        return memberDto;
    }

    public void setMemberDto(MemberDto memberDto) {
        this.memberDto = memberDto;
    }
}
