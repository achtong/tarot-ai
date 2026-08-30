package com.tarot.demo.config;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.tarot.demo.DTO.TarotCardDTO;
import com.tarot.demo.service.TarotRedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TarotTools {
    private final TarotRedisService tarotRedisService;

    @Tool(
        name = "analyzeCareer",
        description = """
        직장운과 관련된 질문을 분석할 때 사용하는 도구입니다.
        이직, 취업, 승진, 업무, 직장생활, 커리어 방향,
        직장 내 인간관계 등의 질문에 사용합니다.
        """
    )
    public String analyzeCareer(
            @ToolParam(description = "사용자의 고민") String concern,
            @ToolParam(description = "질문의 세부 의도") String intent,
            @ToolParam(description = "현재 카드 코드") String mainCardCode,
            @ToolParam(description = "영향 카드 코드") String sub1CardCode,
            @ToolParam(description = "미래 카드 코드") String sub2CardCode) {

            TarotCardDTO main = tarotRedisService.getCard(mainCardCode);
            TarotCardDTO sub1 = tarotRedisService.getCard(sub1CardCode);
            TarotCardDTO sub2 = tarotRedisService.getCard(sub2CardCode);

        return """
            [직장운 분석 기준]

            사용자의 고민:
            %s

            질문의 세부 의도:
            %s

            현재 카드:
            %s

            영향 카드:
            %s

            미래 카드:
            %s

            분석 방법:
            - 현재 카드는 현재 직업 상황을 중심으로 해석한다.
            - 영향 카드는 현재 상황에 영향을 주는 요소를 해석한다.
            - 미래 카드는 앞으로의 흐름을 해석한다.
            - 사용자의 질문 의도와 카드 의미를 연결한다.
            - 카드 키워드를 단순 나열하지 않는다.
            - 세 카드의 관계를 종합해서 해석한다.
            """.formatted(
                concern,
                intent,

                main.getNameKr(),
                main.getKeywordsUp(),
                main.getSummary(),

                sub1.getNameKr(),
                sub1.getKeywordsUp(),
                sub1.getSummary(),

                sub2.getNameKr(),
                sub2.getKeywordsUp(),
                sub2.getSummary()
            );
    }


    @Tool(
        name = "analyzeLove",
        description = """
        연애운과 관련된 질문을 분석할 때 사용하는 도구입니다.
        새로운 만남, 현재 연애, 상대방과의 관계,
        재회, 관계의 흐름 등의 질문에 사용합니다.
        """
    )
    public String analyzeLove(
            @ToolParam(description = "사용자의 고민") String concern,
            @ToolParam(description = "질문의 세부 의도") String intent,
            @ToolParam(description = "현재 카드 코드") String mainCardCode,
            @ToolParam(description = "영향 카드 코드") String sub1CardCode,
            @ToolParam(description = "미래 카드 코드") String sub2CardCode) {
            
            TarotCardDTO main = tarotRedisService.getCard(mainCardCode);
            TarotCardDTO sub1 = tarotRedisService.getCard(sub1CardCode);
            TarotCardDTO sub2 = tarotRedisService.getCard(sub2CardCode);
        return """
            [연애운 분석 기준]

            사용자의 고민:
            %s

            질문의 세부 의도:
            %s

            현재 카드:
            %s

            영향 카드:
            %s

            미래 카드:
            %s

            분석 방법:
            - 현재 카드는 현재 관계의 상황을 중심으로 해석한다.
            - 영향 카드는 관계에 영향을 주는 요소를 해석한다.
            - 미래 카드는 앞으로의 관계 흐름을 해석한다.
            - 사용자의 질문 의도와 카드 의미를 연결한다.
            - 카드 키워드를 단순 나열하지 않는다.
            - 세 카드의 관계를 종합해서 해석한다.
            """.formatted(
                concern,
                intent,

                main.getNameKr(),
                main.getKeywordsUp(),
                main.getSummary(),

                sub1.getNameKr(),
                sub1.getKeywordsUp(),
                sub1.getSummary(),

                sub2.getNameKr(),
                sub2.getKeywordsUp(),
                sub2.getSummary()
            );
    }


    @Tool(
        name = "analyzeWealth",
        description = """
        재물운과 관련된 질문을 분석할 때 사용하는 도구입니다.
        수입, 지출, 투자, 재테크, 재산,
        금전적인 흐름 등의 질문에 사용합니다.
        """
    )
    public String analyzeWealth(
            @ToolParam(description = "사용자의 고민") String concern,
            @ToolParam(description = "질문의 세부 의도") String intent,
            @ToolParam(description = "현재 카드 코드") String mainCardCode,
            @ToolParam(description = "영향 카드 코드") String sub1CardCode,
            @ToolParam(description = "미래 카드 코드") String sub2CardCode) {
            
            TarotCardDTO main = tarotRedisService.getCard(mainCardCode);
            TarotCardDTO sub1 = tarotRedisService.getCard(sub1CardCode);
            TarotCardDTO sub2 = tarotRedisService.getCard(sub2CardCode);

            
        return """
            [재물운 분석 기준]

            사용자의 고민:
            %s

            질문의 세부 의도:
            %s

            현재 카드:
            %s

            영향 카드:
            %s

            미래 카드:
            %s

            분석 방법:
            - 현재 카드는 현재 금전 상황을 중심으로 해석한다.
            - 영향 카드는 금전 상황에 영향을 주는 요소를 해석한다.
            - 미래 카드는 앞으로의 금전 흐름을 해석한다.
            - 사용자의 질문 의도와 카드 의미를 연결한다.
            - 카드 키워드를 단순 나열하지 않는다.
            - 세 카드의 관계를 종합해서 해석한다.
            """.formatted(
                concern,
                intent,

                main.getNameKr(),
                main.getKeywordsUp(),
                main.getSummary(),

                sub1.getNameKr(),
                sub1.getKeywordsUp(),
                sub1.getSummary(),

                sub2.getNameKr(),
                sub2.getKeywordsUp(),
                sub2.getSummary()
            );
    }
}
