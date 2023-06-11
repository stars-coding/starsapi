package com.stars.interfaces.controller;

import cn.hutool.json.JSONUtil;
import com.stars.interfaces.model.entity.IKunParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Random;

/**
 * IKUN控制器
 *
 * @author stars
 */
@RestController
@RequestMapping("/ikun")
public class IKunController {

    public static final String[] iKunContent = new String[]{"黑的都不错啊，嗯，继续，脱粉算我输，我做过最错的事就是和黑粉讲道理，和他们真讲不了理。喜欢黑的继续黑，你越黑我家哥哥就越红，知道为什么怼回去的人少了嘛，因为坤坤告诉我们永远不要和没素质的人去争执 ",
            "我就不明白了现在怎么这么多人黑坤坤，他明明已经很努力了，不信你去听听他的歌曲，看看他的舞蹈。只有这样你才会发自内心的去黑他，而不是跟风",
            "卤出鸡脚了吧！树枝666，爱坤粉 苏珊，食不食油饼？耗丸吗？再黑紫砂吧！4年前的梗你们还在玩，4年前的剩饭你们怎么不吃？臻果粉！我看你们都馍蒸了！蒸梅油酥汁！你们犯法了知道吗？你们再这样我就煲胫了！香精煎鱼是吗？香翅捞饭是吗？真没有荚饺，荔枝？你要我怎么荔枝！？",
            "你们是不是人啊！道歉会不会！你们黑牠黑得那么爽，骂牠骂得那么爽，整天TM没有任何一个人会给牠道歉。你们这样会毁了他的前途，毁了牠的一生！",
            "笑死，你就这么几个表情包，啊？你除了这个表情包你发点新的过来啊，都看腻了好不好，小辣鸡。",
            "私生饭黑粉你们犯法了知道吗，牠很累的，被经常半夜敲门是什么滋味，被私生饭装跟踪器是什么滋味。",
            "告诉你蔡徐坤身上一根毛拔下来都比你好看，都比你有用，都比你有才华，都比你有颜值，都比你有能力。",
            "蔡徐坤到底怎么得罪你们了，为什么要这样对侍他。他真的很好，ikun不是不敢惹事，而是因为他告诉我们无论这个世界怎样我们要永远善良。蔡徐坤是我们ikun的底线，请大家放下偏见去看这个男孩。",
            "鸡那么美你怎么不去娶鸡呢？你那么喜欢说篮球篮球，嫁给篮球得了，把篮球剪个洞套你头上。你会写歌吗？你会作曲吗？你会唱歌吗？啊？你连台都上不去吧，上了台就要尿裤子了吧。",
            "大家好，我是一名爱鲲，我今天想说的是，你们可不可以不要再黑蔡徐坤了，蔡徐坤真的很努力，牠发高烧发到七八十度，都坚持练舞四五个小时，牠真的很努力，你们可以去试着了解一下牠，你们也会爱上牠的，你们也会粉上牠的，不要再玩四字梗好吗，我求求你们这些黑粉，好玩吗？"
    };

    /**
     * 处理POST请求，获取IKUN的回复
     *
     * @param iKunParams 请求体中的IKunParams对象，可选，包含请求参数
     * @param request    请求对象，用于获取请求信息
     * @return 字符串，表示IKUN的回复
     */
    @PostMapping("/xiaoheizi")
    public String getIKun(@RequestBody(required = false) IKunParams iKunParams, HttpServletRequest request) {
        ArrayList<String> list = new ArrayList<>();
        int random = (new Random()).nextInt(10);
        String result = IKunController.iKunContent[random];
        IKunParams resultIKun = new IKunParams();
        resultIKun.setContent(result);
        result = JSONUtil.toJsonStr(resultIKun);
        return result;
    }
}
