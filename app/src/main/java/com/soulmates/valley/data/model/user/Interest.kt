package com.soulmates.valley.data.model.user


data class Interest(
    var interestIdx: Int,
    var isSelected: Boolean,
    var interestName: String
)

object InterestList {
    var positionList = arrayListOf(
        Interest(100, false, "백엔드"),
        Interest(101, false, "프론트엔드"),
        Interest(104, false, "Android"),
        Interest(105, false, "iOS"),
        Interest(102, false, "인프라"),
        Interest(103, false, "DBA"),
        Interest(107, false, "Data engineer"),
        Interest(106, false, "게임개발"),
        Interest(108, false, "머신러닝"),
        Interest(109, false, "QA"),
        Interest(110, false, "보안")
    )

    var techList = arrayListOf(
        Interest(200, false, "javascript"),
        Interest(201, false, "HTML/CSS"),
        Interest(203, false, "Java"),
        Interest(210, false, "kotlin"),
        Interest(213, false, "Swift"),
        Interest(207, false, "C++"),
        Interest(208, false, "C"),
        Interest(204, false, "C#"),
        Interest(202, false, "Python"),
        Interest(220, false, "Django"),
        Interest(221, false, "Flask"),
        Interest(205, false, "PHP"),
        Interest(222, false, "Spring"),
        Interest(223, false, "Node.js"),
        Interest(209, false, "go"),
        Interest(211, false, "Rust"),
        Interest(212, false, "Ruby"),
        Interest(214, false, "R"),
        Interest(215, false, "jQuery"),
        Interest(206, false, "typescript"),
        Interest(216, false, "React.js"),
        Interest(217, false, "Angular"),
        Interest(219, false, "Vue.js"),
        Interest(218, false, "ASP.NET"),
        Interest(224, false, ".NET"),
        Interest(225, false, "Pandas"),
        Interest(226, false, "TensorFlow"),
        Interest(227, false, "Flutter"),
        Interest(229, false, "Spark"),
        Interest(230, false, "Hadoop"),
        Interest(228, false, "Unity"),
        Interest(231, false, "Unreal"),
        Interest(232, false, "NOSQL"),
        Interest(233, false, "RDB"),
        Interest(234, false, "Firebase"),
        Interest(235, false, "Kubernates"),
        Interest(237, false, "Docker"),
        Interest(236, false, "Linux")
    )

    var etcList = arrayListOf(
        Interest(300, false, "공모전"),
        Interest(301, false, "컨퍼런스"),
        Interest(302, false, "해커톤"),
        Interest(303, false, "알고리즘"),
        Interest(304, false, "자격증"),
        Interest(305, false, "IT기기"),
        Interest(306, false, "개발일상")
    )

}