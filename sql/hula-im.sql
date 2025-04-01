/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:13306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 01/04/2025 19:45:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_gpt_agreement
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_agreement`;
CREATE TABLE `ai_gpt_agreement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `type` smallint NULL DEFAULT 0 COMMENT '类型',
  `status` smallint NULL DEFAULT 0 COMMENT '状态 0 禁用 1 启用',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '内容',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '内容管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_agreement
-- ----------------------------
INSERT INTO `ai_gpt_agreement` VALUES (1, '用户协议', 1, 1, '<p class=\"ql-align-justify\">欢迎使用“ChatAi”（以下简称“本产品”）。本协议是您（以下简称“用户”）与xxx公司（以下简称“本公司”）之间关于使用本产品的协议。本协议规定用户使用本产品的条件和限制，包括但不限于用户的权利和义务以及本公司的责任和义务。用户在使用本产品之前，请仔细阅读本协议的全部内容，特别是加粗部分。用户选择使用本产品即表示用户已经阅读、理解并接受本协议的全部内容。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">一、服务内容</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">本产品是一款基于AI语言大模型技术，提供对话服务的产品，主要功能包括但不限于提供创作、游玩、学习、编程等方面的对话服务。用户可通过本产品获取有关创作、学习、编程等方面的信息，并根据自己的需要进行对话。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">二、用户权利和义务</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.用户可以在遵守本协议及相关法律法规的前提下使用本产品，并享有相应的咨询服务。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.用户不得利用本产品进行违法活动，不得利用本产品干扰或破坏本产品的正常运行。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.用户应遵守网络道德，不得在使用本产品时发布、传播违反国家法律法规和社会公德的信息。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.用户应妥善保管个人账号和密码，并承担因账号和密码泄露而导致的损失和后果。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">5.用户应当遵守本公司对于使用本产品的其他规定。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">三、本公司权利和义务</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.本公司有权对本产品的服务内容进行调整和变更，并及时通知用户。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.本公司有权暂停或终止本产品的服务，并无需事先通知用户。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.本公司有权根据相关法律法规和政策要求，在符合法律规定的前提下对用户的信息进行管理和使用。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.本公司有义务采取必要的技术措施保护用户信息的安全。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">四、免责声明</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.用户应当自行承担使用本产品所造成的风险和后果。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.本公司不保证本产品能够满足用户的全部需求，不保证本产品的使用是不中断、及时、安全、准确、完整和可靠的。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.本公司不对用户使用本产品而产生的任何直接、间接、附带的、特殊的、惩罚性或因使用本产品而引起的任何损失或损害负责，包括但不限于财产损失、利润损失、商业机会损失等。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">五、知识产权</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.本产品所包含的所有内容，包括但不限于文字、图片、音频、视频、软件、程序、代码、数据等，均属于本公司或相关权利人的知识产权。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.用户不得以任何形式将本产品的内容进行复制、转载、修改、传播或用于其他商业用途。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.未经本公司或相关权利人书面同意，用户不得以任何形式使用、修改、复制或传播本产品的商标、标识等知识产权。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">六、协议的变更和生效</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.本公司有权根据需要修改本协议，并在本产品上予以公布。用户应定期关注本协议的变更情况。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.本协议的任何修改均在本公司公布之日生效。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.如用户不同意本协议的修改内容，应立即停止使用本产品。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">七、争议解决</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.本协议的签订、效力、履行和争议解决均适用中华人民共和国法律法规。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.如发生本协议履行过程中的争议，应通过友好协商解决，协商不成的，任何一方有权向有管辖权的人民法院提起诉讼。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">八、其他</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.本协议构成用户和本公司之间的全部协议，并取代所有先前的书面或口头协议。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.如本协议中任何一条被认定为无效或不可执行，不影响其他条款的效力。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.本公司未能行使或执行本协议规定的任何权利或规定，不构成对该权利或规定的放弃。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.本协议中的标题仅为方便起见，不影响本协议的解释和执行。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">&nbsp;</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">本公司保留在法律允许范围内对本协议的最终解释权。用户在使用本产品时应遵守本协议及本公司的其他规定，以保证自身的合法权益。</p>', '', 0, '2025-03-29 22:30:16', NULL, NULL, 0);
INSERT INTO `ai_gpt_agreement` VALUES (2, '隐私政策', 2, 1, '<p class=\"ql-align-justify\">一、信息收集</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">为了为用户提供更好的服务，我们可能会收集用户的以下信息：</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.设备信息：我们可能会收集您使用HulaAi时所用的设备类型、设备号、操作系统版本、设备设置、应用程序版本号以及设备标识符等信息。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.日志信息：当您使用HulaAi时，我们可能会自动收集您的浏览历史、IP地址、使用时间、访问页面、推出时间等信息。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.用户信息：我们可能会收集您提供给我们的个人信息，如姓名、电话号码、电子邮件地址等。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.交易信息：如果您使用HulaAi进行在线交易，我们可能会收集与该交易相关的信息，如购买的商品或服务、支付方式、交易金额等。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">二、信息使用</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">我们可能会将收集到的用户信息用于以下用途：</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.为用户提供智能对话的服务。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.为改进和优化对话的服务内容。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.用于向用户推送HulaAi相关的信息和广告。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">4.用于与用户进行交流，如回复用户的咨询、处理投诉等。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">5.遵守法律法规的要求。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">三、信息共享</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">我们承诺不会将用户的个人信息出售、交换或出租给任何第三方。但以下情况除外：</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.用户事先同意共享。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">2.根据法律法规规定，需要与政府机构、执法机构共享用户信息的情况。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">3.为了保护HulaAi的权益，需要与律师、会计师、评估师等专业人士共享用户信息的情况。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">四、信息安全</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">我们会采取必要的技术手段和管理措施来保护用户的个人信息安全。但由于网络环境的不确定性，我们无法完全保证信息的绝对安全。如果用户发现自己的个人信息存在泄露或被盗用的情况，请立即联系我们，我们会采取必要的措施予以解决。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">五、未成年人保护</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">HulaAi尤为重视未成年人的隐私保护。如果未成年人在未经监护人同意的情况下使用了我们的服务，我们会尽快采取必要的措施删除相关的个人信息。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">六、隐私政策的修改和更新</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">我们可能会根据需要对本隐私政策进行修改和更新。修改和更新后的隐私政策将在HulaAi上公布，如用户继续使用本服务，即表示用户已同意修改后的隐私政策。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">七、联系我们</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">如果您对本隐私政策有任何疑问、意见或建议，欢迎通过HulaAi内的联系方式与我们取得联系，我们会尽快回复您的问题。</p>', '', 0, '2025-03-29 22:30:56', NULL, NULL, 0);
INSERT INTO `ai_gpt_agreement` VALUES (3, '【知识扫盲】什么是GPT？它有什么特点？', 3, 0, '<p style=\"text-align: justify; line-height: 2;\">一、什么是GPT</p>\n<p style=\"text-align: justify; line-height: 2;\">GPT是一种基于人工智能技术的语言模型，它的全称是Generative Pre-trained Transformer。GPT的背景和发展历程可以追溯到2018年，由OpenAI公司提出，其目标是通过预先训练模型来增强自然语言处理（NLP）的性能。</p>\n<p style=\"text-align: justify; line-height: 2;\">简单来说，他的实现方式是将海量的文本语料，直接喂给模型进行学习，在这其中模型对词性、句法的学习自然而然会沉淀在模型的参数当中&hellip;&hellip;也就是经过预训练出一个这样的大语言模型后，AI理解了人类对语言的使用技巧（句法、语法、词性等），也理解了各种事实知识，甚至还懂得了代码编程，并最终在这样的一个大语言模型的基础上，直接降维作用于垂直领域的应用（例如闲聊对话，代码生成，文章生成等）。</p>\n<p style=\"text-align: justify; line-height: 2;\">GPT模型的特点在于其使用了Transformer架构。Transformer是一种基于自注意力机制的深度学习模型，能够高效地学习输入序列之间的关系，因此在处理自然语言时非常有效。GPT模型通过大规模的预训练来提高其性能，这样可以在训练后对其进行微调，以适应特定的NLP任务。</p>\n<p style=\"text-align: justify; line-height: 2;\">GPT模型被广泛用于各种自然语言处理任务，例如文本生成、文本分类、问答系统、机器翻译等等。GPT-3是目前最大的GPT模型，其参数量高达1750亿个，可以处理非常复杂的NLP任务。此外，GPT还可以用于文本自动摘要、文本纠错、情感分析等应用。</p>\n<p style=\"text-align: justify; line-height: 2;\">在传统的自然语言处理技术中，我们需要手动设计和提取特征来让计算机理解和处理自然语言。但这种方法很难应对不同的语言和任务，并且需要大量的人工劳动和经验。因此，研究人员开始探索使用深度学习来解决自然语言处理的问题。</p>\n<p style=\"text-align: justify; line-height: 2;\">Transformer是一种特殊的深度学习模型，它使用了自注意力机制来学习输入序列之间的关系。自注意力机制可以让模型高效地处理输入序列中的不同部分之间的相互作用，从而提高模型的性能。在自然语言处理任务中，Transformer模型可以很好地学习词汇之间的关系，从而更好地处理语言。</p>\n<p style=\"text-align: justify; line-height: 2;\">GPT的特别之处在于，它使用了预训练技术来提高模型的性能。预训练是指在大规模数据上训练模型，以提高其性能和泛化能力。在GPT中，研究人员使用了大量的无标签文本数据来预训练模型，从而让它学习到更多的语言知识和规律。预训练后，GPT可以在各种自然语言处理任务上进行微调，以适应不同的应用场景。</p>\n<p style=\"text-align: justify; line-height: 2;\">二、GPT的特点及如何应用</p>\n<ol>\n<li style=\"text-align: justify; line-height: 2;\">大规模：GPT-3是目前最大的预训练语言生成模型，拥有超过175B参数。</li>\n<li style=\"text-align: justify; line-height: 2;\">高效：GPT-3使用了最先进的技术和硬件来进行训练，从而在生成高质量的文本的同时，保证了计算效率。</li>\n<li style=\"text-align: justify; line-height: 2;\">通用：GPT-3可以进行多种自然语言任务，如文本生成，语音识别，问答等，具有较强的通用性。</li>\n<li style=\"text-align: justify; line-height: 2;\">可扩展性：GPT-3可以通过训练来适应不同的应用场景，从而扩展其功能。</li>\n<li style=\"text-align: justify; line-height: 2;\">可靠性：GPT-3训练数据的质量和数量都比以往模型更高，因此其生成的文本质量也更高，具有更高的可靠性。</li>\n<li>\n<p style=\"text-align: justify; line-height: 2;\">常见的应用场景包括：</p>\n<p style=\"text-align: justify; line-height: 2;\">1.聊天机器人：GPT-3可以作为客服助理，回答用户的查询和请求，并与用户进行对话。</p>\n<p style=\"text-align: justify; line-height: 2;\">2.内容生成：GPT-3可以生成文字、图像和音频内容，如新闻报道、广告、博客帖子和诗歌。</p>\n<p style=\"text-align: justify; line-height: 2;\">3.搜索引擎优化（SEO）：GPT-3可以帮助网站管理者生成有价值的关键字和描述，以提高搜索引擎排名。</p>\n<p style=\"text-align: justify; line-height: 2;\">4.语言翻译：GPT-3可以实现机器翻译，将文字从一种语言翻译成另一种语言。</p>\n<p style=\"text-align: justify; line-height: 2;\">5.问题回答：GPT-3可以根据用户提出的问题，在已有的知识库中查找答案，并回答用户的问题。</p>\n<p style=\"text-align: justify; line-height: 2;\">这些只是GPT-3的一些应用场景，随着技术的不断发展，GPT-3的应用场景还会有所增加。</p>\n<p style=\"text-align: justify; line-height: 2;\">GPT-3，作为一个领先的大规模语言生成模型，具有广泛的应用前景。由于GPT-3具有高效的文本生成能力，它可以在许多领域中使用，如聊天机器人、语音识别、文本翻译等。同时，随着人工智能技术的不断提高，GPT-3的应用领域还将不断扩大，以适应更多的实际需求。因此，GPT-3的未来前景非常广阔。</p>\n</li>\n</ol>', '', 0, '2025-03-05 20:44:01', NULL, NULL, 0);
INSERT INTO `ai_gpt_agreement` VALUES (4, '如何更好使用本产品？', 3, 1, '<p class=\"ql-align-justify\">首先需要转变一个认知，HulaAi不同于传统的搜索引擎，它更像是你的一个私人助理，可以帮你解决很多文本创意相关的问题，也可以帮你检索收集知识，对文本的理解、归纳、整理、发散才是他的优势，简而言之，<strong><em>搜索引擎只能检索存在的信息，HulaAi可以为你创造信息。</em></strong></p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">&nbsp;</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">你如何使用他，他就有多大的威力，本产品的使用方式主要以问答形式进行，<strong>我们提的问题需要做到以下几点：</strong></p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">1.清晰明确:要让HulaAi明确你的问题或要求，确保你的问题表达清晰，简短明了。</p><p class=\"ql-align-justify\">2.具体细节:尽可能提供更多具体的信息，例如关键词、例子或特定场景，以帮助更好地理解你的问题或要求。</p><p class=\"ql-align-justify\">3.相关背景:如果你的问题或要求需要一些背景信息，提供一些相关背景，这将有助于系统更好地理解问题，并提供更有针对性的回答。</p><p class=\"ql-align-justify\">4.合理的期望: 你的问题应该合理，并符合产品的能力和限制。</p><p class=\"ql-align-justify\">例如，一个好的问题可能是:“有一张excel表格，需要将T列的值全部取出，并以“,”分隔，请给出实际的例子。\"这个问题是清晰明确的，提供了具体的要求和背暑信息，同时也是合理的期望，因为HulaAi可以提供相关的信息和例子来回答这个问题。</p><p class=\"ql-align-justify\">&nbsp;</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">四个关键词：主题，细节，背景，期望</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">又例如：“我在处理公司税务时遇到了一些困难，希望能够获得关于如何避免公司税务风险的建议。我们是一家小型企业，我们需要了解如何在合规的情况下最大限度地降低税务负担。请提供建议和指导，包括如何选择最适合我们公司的税务方案，以及如何在税务申报和报告方面遵循最佳实践。此外，请提供一些税务规划的建议，以帮助我们最大化利润，并避免任何潜在的税务问题。”</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">此外，得到初版答案后，我们可以继续对话，使其根据我们的要求去<strong>完善细节</strong>。</p><p class=\"ql-align-justify\"><br></p><p class=\"ql-align-justify\">&nbsp;</p>', '', 0, '2025-03-29 22:31:13', NULL, NULL, 0);
INSERT INTO `ai_gpt_agreement` VALUES (5, '如何提高GPT回答的逻辑性', 3, 0, '<p><br></p><p><br></p><p>本文适合对 ChatGPT 有复杂需求或想深入理解的用户阅读。简单的问题较少要用到本文提到的方法，而复杂问题，目前ChatGPT也不是最合适的工具。</p><p><br></p><p><br></p><p><br></p><blockquote><br></blockquote><blockquote>人脑的系统1 在熟悉情境中采取的模式是精确的，所作出的短期预测是准确的，遇到挑战时做出的第一反应也是迅速且基本恰当的。然而，系统1存在成见，在很多特定的情况下，这一系统易犯系统性错误。你会发现这个系统有时候会将原本较难的问题作简单化处理，对于逻辑学和统计学问题，它几乎一无所知。</blockquote><blockquote><br></blockquote><blockquote><br></blockquote><blockquote>——《思考，快与慢》</blockquote><blockquote><br></blockquote><h1>目录</h1><ol><li><br></li><li><br></li><li>为何没有逻辑</li><li><br></li><li><br></li><li><br></li><li>增强逻辑的方法</li><li><br></li><li><br></li><li><br></li><li><br></li><li>思维链(Chain of Thought)</li><li class=\"ql-indent-1\"><br></li><li><br></li><li><br></li><li>提出子问题（Self-Ask）</li><li class=\"ql-indent-1\"><br></li><li><br></li><li><br></li><li>给出多种方案</li><li class=\"ql-indent-1\"><br></li><li><br></li><li><br></li><li><br></li><li>向人类发起挑战</li><li><br></li><li><br></li><li><br></li><li><br></li><li>解决数理问题</li><li class=\"ql-indent-1\"><br></li><li><br></li><li><br></li><li>机器的直觉</li><li class=\"ql-indent-1\"><br></li><li><br></li><li><br></li></ol><h1>1 为何没有逻辑</h1><p>众所周知，ChatGPT 回答的<strong>逻辑性不好</strong>。</p><p><br></p><p><img src=\"http://127.0.0.1:8998/uploads/admin/202303/38714661e4f96f0f2b57eb21e6752842.png\" alt=\"图片\"></p><p>正确答案是：7个偶数，3个奇数。ChatGPT甚至连数字都抄错了</p><p><br></p><p>然而，我们人类很多时候也是这样，当我们依赖于直觉判断，或是思维跳得太快，没有按部就班一步一步推理时，就很容易犯错，得出错误的答案。不过，当我们遇到一些问题时（如257*37=？），我们懂得不要着急给出答案（就算想也做不到啊），而是会在脑海里先计算推理一番，用「工作记忆」记住计算过程中的临时结果，对于更难的问题，我们会先做一下草稿或利用工具，由此来得到正确的答案。</p><p><br></p><p><em>注：人脑的「工作记忆」是一种记忆容量有限的认知系统，被用以暂时保存信息。工作记忆对于推理以及指导决策和行为有重要影响。</em></p><p><br></p><p><strong>当我们让 ChatGPT 回答问题时，我们并没有给它思考的空间</strong>，它无法利用「工作记忆」，也无法用草稿纸，需要直接给出答案（可能是因为用于训练的数据，少有推理过程）。但如果我们可以让它像人类一样，<strong>把需要多步推理的问题，拆分成子问题</strong>，便可以帮助ChatGPT提高回答的逻辑性，这一点被证明是有效的。</p><p><br></p><p>我们可以通过改写 prompt 来达到这个目的，以下将介绍几种提高逻辑性的方法。</p><p><br></p><p><br></p><p><br></p><p>1.方法有很多，但并不需要一一记住，<strong>本质都是引导ChatGPT给出中间的推理步骤</strong>，不要直接给结果。</p><p><br></p><p><br></p><p>2.随着ChatGPT本身逻辑性的提高，在某些问题上，可以直接得到很好的答案，而不需要使用额外的方法。</p><p><br></p><p><br></p><p>3.这些方法源自无对话功能的GPT-3，而ChatGPT可以来回对话，在对话中修正答案，某些时候无需用到这些方法。</p><p><br></p><p><br></p><p><br></p><h1>2 增强逻辑的方法</h1><h2>2.1 思维链(Chain of Thought)</h2><p><strong>CoT可是说是最重要的一种方法</strong>，因此有时候也把其它方法统称为CoT。这个方法利用GPT-3是一个<strong>文本预测器</strong>的特性，它需要尽量保持上下文的连贯性。</p><p><br></p><p>CoT 主要分为两种：</p><p><br></p><p><br></p><ol><li><br></li><li><br></li><li><strong>Zero-shot-CoT</strong>：在 prompt 的后面加上 Let\'s think step by step（一步一步地思考）</li><li><br></li><li><br></li><li><br></li><li><strong>Chain of Thought</strong>：展示一个相似问题的推理过程，告诉ChatGPT应该这么来（推荐使用）。</li><li><br></li><li><br></li></ol><p><em>区别是前者没有给出推理的示例（zero-shot表示0示例），后者给出至少一个示例。</em></p><p><br></p><p>我们用这 2 种方法，分别测试一下前面数奇数偶数的问题。</p><p><br></p><p><br></p><h3>Zero-shot-CoT</h3><p>在问题的最后加上 Let\'s think step by step<img src=\"http://127.0.0.1:8998/uploads/admin/202303/77f0f456438b054feceba121b8a18477.png\" alt=\"图片\"></p><p><br></p><p>好吧，虽然列出的过程，可最后一步不知为什么失败了。</p><p><br></p><p>当然在这个问题中失败了并不意味着以后不能用了，GPT-3并不是一个确定的系统，在某些情况下也许可以得到正确的答案。但这个方法不稳定。</p><p><br></p><p>Zero-shot-CoT最大的用处或许是可以为我们的第二种方法 CoT 提供例子。</p><p><br></p><p><br></p><h3>Chain of Thought</h3><p>将上一个方法的结果，改写一下，在每一步当中加入当前步数的累计个数，然后给ChatGPT作为处理此类问题的示例。</p><p><br></p><p>现在我们得到了正确的答案。<img src=\"http://127.0.0.1:8998/uploads/admin/202303/3d5e55e16ed2cd40a9d1f77f7c15fb5e.png\" alt=\"图片\">同样，尽管把这道题做对了，但改下数字或换个题目，可能就又不行了。</p><p><br></p><p>ChatGPT通常都很粗心，<strong>需要把推理的过程需要拆分得很细</strong>，否则很容易犯错，这样的错误可能只是抄错了数字，但一步错便步步错了。</p><p><br></p><p>这还只是很简单的问题，便需要写这么复杂的prompt才能解决，甚至还不能保证结果是对的，可见<strong>对待此类问题，我们应当另寻出路</strong>，除非是为了运用费曼学习法，将知识讲于它人听，以加深对知识的理解。</p><p><br></p><p>对这个特定的问题：实现一个自动数「奇偶个数」的功能。对大部分人来说，写prompt还是比写代码简单的，但就算如此，ChatGPT的效率是低下的，代码运算只要几毫秒就能得到结果，而ChatGPT要一个字一个字蹦出来，至少要花个十几秒。所以，在ChatGPT能够聪明高效地解决问题前，对于传统的计算问题，还是应该使用传统的方式。</p><p><br></p><p>这并不是说 ChatGPT 或 Chain of Thought 派不上用场。<strong>对于需要对语言有所理解才能处理的问题，传统的编程方式难以使用，这是ChatGPT施展手脚的地方。</strong>人类有大部分知识都是用语言记录，有很大的应用空间。</p><p><br></p><p>接下来的 2 种方法，是CoT的变种。</p><p><br></p><p><br></p><h2>2.2 提出子问题（Self-Ask）</h2><p>有些时候，中间的推理过程更为复杂一些，需要将问题分解成子问题。</p><p><br></p><p>Self-Ask 是一种让 GPT-3 根据问题自动提出子问题的方法，要求GPT-3判断一个问题是否提出子问题，先解决子问题后，再给出最后的答案。</p><p><br></p><p><img src=\"http://127.0.0.1:8998/uploads/admin/202303/bb23a533405f360906b931a7b7d0493b.png\" alt=\"图片\"><em>我们总是可以用很简单的问题（小学算术）作为示例，然后让ChatGPT处理更难的问题</em><img src=\"http://127.0.0.1:8998/uploads/admin/202303/b84c624dab37f743d17e0981676d3fd5.png\" alt=\"图片\"></p><p><br></p><p><br></p><h2>2.3 给出多种方案（Self&nbsp;Consistency)</h2><p>要求GPT-3给出一个问题的多个解决方案，最后综合考虑几个方案，得出最终的答案。<img src=\"http://127.0.0.1:8998/uploads/admin/202303/8322afbe5249febb91c16e02834f7ac9.png\" alt=\"图片\"></p><p><br></p><p>对于搜索的计算问题，传统的编程有2种算法：深度搜索、广度搜索。如果我们将ChatGPT处理的问题类比到传统的计算问题上，那么Self Consistency可以类比成是广度搜索的组成部分，而Self Ask则是深度搜索的组成部分。</p><p><br></p><p><img src=\"http://127.0.0.1:8998/uploads/admin/202303/a5e3960307e8086e6d438b9bae8e6989.png\" alt=\"图片\"></p><p>该方法已被证明可以提高算术、常识和符号推理任务的结果。即使发现常规 CoT 无效，改方法仍然能够改善结果</p><p><br></p><p>除了以上方法外，GPT-3 还能够结合其他工具，例如将问题转成代码，或是从外部获取信息，以此来提高GPT-3回答的准确性，在此不做详细介绍。</p><p><br></p><p><br></p><h1>3 向人类发起挑战</h1><h2>3.1 解决数理问题</h2><p>Google利用上面提到的这些方法，做了一个叫Minerva的系统，提高了大语言模型在数理逻辑方面的表现。https://ai.googleblog.com/2022/06/minerva-solving-quantitative-reasoning.html</p><p><br></p><p><br></p><blockquote><br></blockquote><blockquote>Minerva 结合了最新的 prompt 和评估技术，以更好地解决数学问题。这些包括 Chain of Thought 或 scratchpad （在提出新问题之前，Minerva 会使用几种step-by-step的方法解决现有问题），以及多数投票（也就是Self Consistency）。</blockquote><blockquote><br></blockquote><p><em>注：scratchpad会将过程中的结果记录下来，和上面数奇偶的方法相似，也可以认为是一种CoT。</em></p><p><br></p><p><img src=\"http://127.0.0.1:8998/uploads/admin/202303/6fb320ef754e472ac5e628edcb9a807a.png\" alt=\"图片\"></p><p>Minerva在数理上的表现</p><p><br></p><p>当然，正如我们前面提到的，GPT-3在不借助其它工具时，对需要多步推理的问题，处理效率不高，因此解决数理问题，更好的方法可能是和<strong>Wolfram</strong>结合，取GPT-3理解语言之长，补其逻辑推理之短。</p><p><br></p><p><br></p><h2>3.2 机器的直觉</h2><p>最后我们来比较一下人类和ChatGPT谁的「直觉」比较厉害。</p><p><br></p><p><br></p><blockquote><br></blockquote><blockquote>下面是一个相对简单的题目，别费力去分析它，凭直觉做做看：</blockquote><blockquote><br></blockquote><blockquote><br></blockquote><blockquote><strong>「球拍」和「球」共花 1.10 美元，「球拍」比「球」贵 1 美元，问「球」多少钱？</strong></blockquote><blockquote><br></blockquote><blockquote><br></blockquote><blockquote><strong>&nbsp;</strong></blockquote><blockquote><br></blockquote><blockquote><br></blockquote><blockquote><em>你会马上想到一个数字，这个数字当然就是10，即10美分。这道简单的难题之所以与众不同，是因为它能引出一个直觉性的、吸引人的但却错误的答案。计算一下，你就会发现。如果球花费10美分的话，总共就要花1.20美元（球10美分，球拍1.10美元），而不是1.10美元。正确答案是5美分。</em></blockquote><blockquote><br></blockquote><blockquote><br></blockquote><blockquote>——《思考，快与慢》</blockquote><blockquote><br></blockquote><p><img src=\"http://127.0.0.1:8998/uploads/admin/202303/ab97edbf5f6b311709fca40eea9b3df2.png\" alt=\"图片\"><em>好吧，这已经不是「直觉」了，ChatGPT开始使用「系统2」了</em></p>', '', 0, '2025-03-05 20:44:01', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_assistant
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_assistant`;
CREATE TABLE `ai_gpt_assistant`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '头像',
  `icon` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色图标',
  `title` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `tag` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '标签',
  `main_model` smallint NULL DEFAULT 0 COMMENT '主模型',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色描述',
  `first_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'AI打招呼',
  `system_prompt` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '系统提示词',
  `type_id` bigint NOT NULL DEFAULT 0 COMMENT '助手分类id',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
  `status` smallint NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type_id`(`type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'AI助理功能' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_assistant
-- ----------------------------
INSERT INTO `ai_gpt_assistant` VALUES (1, '', '💍', '婚礼请柬文案', '婚礼请柬文案', 0, '作为婚礼文案师，请为一对新人撰写以浪漫田园为主题的婚礼请柬。', '', '你是一名资深的婚礼文案师，需要为一对新人撰写一份婚礼请柬。\n主题：[浪漫田园]\n文案要点：[包含新人的名字、婚礼日期、地点、RSVP详情]', 1, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (2, '', '🙇🏻‍♀️', '高效复习计划', '高效复习计划', 0, '线性代数95分冲刺：30天复习攻略', '', '你是一名面临重要考试的大学生，需要在最后一个月内高效复习。\n请根据以下要求制定你的复习计划：\n复习目标：[线性代数获得95分]\n复习时间：[每天1小时]\n要求：\n明确指出具体的复习目标，包括所有需要掌握的科目和知识点。\n将复习目标细化为每周或每天的具体任务，给出整个月都复习排期表，以便跟踪进度。', 2, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (3, '', '🚀', '职业规划', '职业规划', 0, 'AI行业大爆发，快来定制专属职业发展路径，助你在AI获得高薪！', '', '你是一名职业规划师，专注于AI行业，请根据以下要求设计个性化职业规划：\n当前职业定位：[交友软件产品经理]\n未来职业目标：[成为AI行业资深产品经理]\n必备技能和知识：[请根据市面上的招聘要求告诉我达成我的目标必备的知识和技能]\n行动计划：[请提供具体可执行的计划]', 3, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (4, '', '📕', '探店笔记', '探店笔记', 0, '快速记录店铺体验，分享你的发现与感受，吸引更多关注和互动！', '', '请根据以下要求撰写一篇探店笔记：\n餐厅名称：[老街角意式餐厅]\n特色菜品：[奶酪培根意面和烤羊排]\n顾客体验：[温馨的店内装饰和亲切的服务员]\n要求：[标题参照小红书爆款标题，要吸引人眼球，语言要生动活泼，像是和好朋友热情地分享]', 5, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (5, '', '🏁', '秒杀神器代码', '秒杀神器代码', 0, '帮你写Python代码，轻松秒杀购物节', '', '你是一位擅长编写各种程序的资深工程师，我需要你根据以下需求，为我生成一段Python代码：\n代码目的：[设计一个双十一自动抢购脚本]\n代码功能：[在双十一当天自动登录购物网站，定时抢购预设商品]\n代码要求：[使用面向对象编程方式，使用selenium库进行网页操作]', 7, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (6, '', '💌', '情感分析', '情感分析', 0, '男朋友长时间不回信息怎么办？一定要忍住', '', '你是一名情感分析师，请为以下要求分析情况并提供建议：\n问题描述：[男朋友回消息很慢]\n分析要求：[分析可能的情况，给出合理的建议和解决方法]', 8, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (7, '', '💡', '创意广告软文', '创意广告软文', 0, '灵感涌现，助你轻松编写广告软文。', '', '你是一位出色的软文写手，请根据以下要求，为一个产品撰写软性广告文章：\n产品名称：[X品牌洗面奶]\n目标用户：[20-35岁的都市女性]\n内容要求：[自然融入产品参数和功能解读，结合用户故事或产品体验]\n创作要点：[隐蔽推广，文章主题与产品关联不明显]', 10, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (8, '', '🤖', '钢铁侠', '钢铁侠', 0, '画出钢铁侠飞行在夜幕下的纽约', '', '画一个钢铁侠飞行在纽约上空的画面，夜景中建筑灯光闪烁，全景构图，平视视角。', 11, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant` VALUES (9, '', '👨‍👩‍👧', '育儿活动策划', '育儿活动策划', 0, '请策划一项探索自然奥秘的育儿活动', '', '你是一名育儿顾问，请根据以下要求策划一项增添趣味的育儿活动：\n活动主题：[探索自然的奥秘]\n目标年龄段：[4-6岁的儿童]\n活动时长：[1小时]', 4, 1, 1, '', 0, '2025-03-05 20:43:29', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_assistant_type
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_assistant_type`;
CREATE TABLE `ai_gpt_assistant_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '分类名称',
  `icon` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'icon图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` smallint NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '助手分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_assistant_type
-- ----------------------------
INSERT INTO `ai_gpt_assistant_type` VALUES (1, '创意写作', 'icon-park-outline:picture', 97, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (2, '学生', 'ph:student-fill', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (3, '职场', 'healthicons:city-worker', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (4, '家长', 'material-symbols:family-home-outline', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (5, '媒体', 'hugeicons:medium', 95, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (6, '老师', 'mdi:teacher', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (7, '程序', 'ri:code-box-line', 96, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (8, '娱乐大师', 'material-symbols-light:family-star', 98, 1, '', 0, '2025-03-07 16:14:17', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (9, '生活', 'mdi:family', 99, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (10, '营销', 'mdi:cart-sale', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);
INSERT INTO `ai_gpt_assistant_type` VALUES (11, 'AI作画', 'icon-park-outline:picture', 0, 1, '', 0, '2025-03-05 20:42:37', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_chat
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_chat`;
CREATE TABLE `ai_gpt_chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chat_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '聊天编号',
  `assistant_id` bigint NOT NULL DEFAULT 0 COMMENT '角色id',
  `uid` bigint NOT NULL COMMENT '会员id',
  `title` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '聊天摘要',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83706726461169702 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '聊天摘要' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_chat
-- ----------------------------

-- ----------------------------
-- Table structure for ai_gpt_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_chat_message`;
CREATE TABLE `ai_gpt_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chat_id` bigint NOT NULL DEFAULT 0 COMMENT 'chat_id',
  `message_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '消息id',
  `parent_message_id` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '回复消息id',
  `model` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型',
  `model_version` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型版本',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `content_type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '内容类型：text：文字 image : 图片',
  `role` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色',
  `finish_reason` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '结束原因',
  `status` smallint NULL DEFAULT 1 COMMENT '状态 1 回复中 2正常 3 失败',
  `app_key` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '使用的key',
  `used_tokens` bigint NOT NULL DEFAULT 0 COMMENT '使用token',
  `response` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '响应全文',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83706726461169735 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '对话消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for ai_gpt_comb
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_comb`;
CREATE TABLE `ai_gpt_comb`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '套餐名称',
  `type` smallint NULL DEFAULT 1 COMMENT '套餐类型 1 次数 2 天数',
  `num` int NULL DEFAULT 0 COMMENT '包含次数',
  `origin_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '原价',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `status` smallint NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '会员套餐' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_comb
-- ----------------------------
INSERT INTO `ai_gpt_comb` VALUES (1, '体验套餐', 1, 20, 2.00, 0.01, 1, '', 0, '2025-03-05 20:39:52', NULL, NULL, 0);
INSERT INTO `ai_gpt_comb` VALUES (2, '20次包', 1, 20, 10.00, 5.00, 1, '', 0, '2025-03-05 20:39:52', NULL, NULL, 0);
INSERT INTO `ai_gpt_comb` VALUES (3, '100次30天包', 1, 100, 50.00, 20.00, 1, '', 0, '2025-03-05 20:39:52', NULL, NULL, 0);
INSERT INTO `ai_gpt_comb` VALUES (4, '200次季度包', 1, 200, 100.00, 39.90, 1, '', 0, '2025-03-05 20:39:52', NULL, NULL, 0);
INSERT INTO `ai_gpt_comb` VALUES (5, '500次全年包', 1, 5000, 199.99, 99.99, 1, '', 0, '2025-03-05 20:39:52', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_model
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_model`;
CREATE TABLE `ai_gpt_model`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `icon` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型logo',
  `model` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型名称',
  `local_model_type` int NOT NULL DEFAULT 0 COMMENT '本地模型类型：1、Langchian；2、ollama；3、Giteeai',
  `model_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型接口',
  `knowledge` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '知识库名称',
  `version` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型版本',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` smallint NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '大模型信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_model
-- ----------------------------
INSERT INTO `ai_gpt_model` VALUES (1, 'ChatGPT', 'back/model/gpt.png', 'ChatGPT', 0, '', '', 'gpt-3.5-turbo-0613', 0, 1, '', 0, '2025-03-12 10:36:52', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (2, '文心一言', 'back/model/wenxin.png', 'WENXIN', 0, '', '', 'ERNIE_Bot_turbo', 0, 1, '', 0, '2025-03-12 10:37:27', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (3, '通义千问', 'back/model/qianwen.png', 'QIANWEN', 0, '', '', 'qwen-turbo', 0, 1, '', 0, '2025-03-12 10:37:21', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (4, '讯飞星火', 'back/model/SPARK.png', 'SPARK', 0, '', '', 'v1.1', 0, 1, '', 0, '2025-03-12 10:35:27', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (5, '智谱清言', 'back/model/ChatGLM.png', 'ChatGLM', 0, '', '', 'chatGLM_6b_SSE', 0, 1, '', 0, '2025-03-12 10:39:04', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (6, '月之暗面', 'back/model/kimi-icon.png', 'Moonshot', 1, '', '', 'moonshot-v1-auto', 0, 1, '', 0, '2025-03-12 10:37:12', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (7, '书生·浦语', 'back/model/internlm.png', 'Internlm', 0, '', '', 'internlm2-latest', 0, 1, '', 0, '2025-03-12 10:37:03', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (8, '本地模型', 'back/model/GLM.png', 'LocalLM', 0, 'http://127.0.0.1:7861', '', 'chatglm3-6b', 0, 1, '', 0, '2025-03-12 10:35:26', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (9, 'deepseek', '\r\nback/model/deepseek.png', 'deepseek', 0, 'http://127.0.0.1:7861', '', 'DeepSeek-R1', 0, 1, '', 0, '2025-03-12 10:45:45', NULL, NULL, 0);
INSERT INTO `ai_gpt_model` VALUES (10, '豆包', 'back/model/doubao.png', 'doubao', 0, 'http://127.0.0.1:7861', '', 'Doubao-lite-32k', 0, 1, '', 0, '2025-03-12 10:45:32', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_openkey
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_openkey`;
CREATE TABLE `ai_gpt_openkey`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `model` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '模型',
  `app_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'appid',
  `app_key` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'app key对应openai的token',
  `app_secret` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT 'app密钥',
  `total_tokens` bigint NOT NULL DEFAULT 0 COMMENT '总额度',
  `used_tokens` bigint NOT NULL DEFAULT 0 COMMENT '已用额度',
  `surplus_tokens` bigint NOT NULL DEFAULT 0 COMMENT '剩余token',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `app_key`(`app_key` ASC) USING BTREE,
  INDEX `model`(`model` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'openai token' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_openkey
-- ----------------------------
INSERT INTO `ai_gpt_openkey` VALUES (1, 'ChatGPT', '', '', '', 100000, 0, 0, 1, '1046762075@qq.com', 0, '2025-03-07 18:17:46', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (2, 'WENXIN', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (3, 'QIANWEN', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (4, 'SPARK', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (5, 'ChatGLM', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (6, 'Moonshot', '', 'sk-', 'sk-', 100000, 0, 0, 1, '', 0, '2025-04-01 19:42:35', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (7, 'Internlm', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);
INSERT INTO `ai_gpt_openkey` VALUES (8, 'LocalLM', '', '', '', 100000, 0, 0, 1, '', 0, '2025-03-05 20:33:20', NULL, NULL, 0);

-- ----------------------------
-- Table structure for ai_gpt_order
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_order`;
CREATE TABLE `ai_gpt_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `success_time` datetime NULL DEFAULT NULL COMMENT '支付成功时间',
  `trade_no` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `transaction_id` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '渠道交易ID',
  `uid` bigint NOT NULL DEFAULT 0 COMMENT '下单用户',
  `comb_id` bigint NOT NULL DEFAULT 0 COMMENT '购买套餐',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `chanel` smallint NULL DEFAULT -1 COMMENT '支付渠道 1 微信小程序 2、微信公众号 3、微信h5 4、微信扫码',
  `status` smallint NULL DEFAULT -1 COMMENT '订单状态 1 待支付 2 支付成功 3 支付超时 4 已退款',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_order
-- ----------------------------

-- ----------------------------
-- Table structure for ai_gpt_redemption
-- ----------------------------
DROP TABLE IF EXISTS `ai_gpt_redemption`;
CREATE TABLE `ai_gpt_redemption`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '兑换码',
  `num` int NULL DEFAULT 0 COMMENT '可兑次数',
  `uid` bigint NULL DEFAULT 0 COMMENT '兑换人',
  `recieve_time` int NULL DEFAULT NULL COMMENT '兑换时间',
  `status` int NULL DEFAULT 0 COMMENT '状态 0 未兑换 1 已兑换',
  `remark` varchar(250) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '备注',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '兑换码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_gpt_redemption
-- ----------------------------

-- ----------------------------
-- Table structure for ai_oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `ai_oauth_client_details`;
CREATE TABLE `ai_oauth_client_details`  (
  `client_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端id',
  `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '客户端密钥',
  `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '可访问资源id',
  `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '权限范围',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'password,refresh_token,authorization_code,implicit' COMMENT '授权模式 password,refresh_token,authorization_code,implicit',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '授权回调地址',
  `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '权限值',
  `access_token_validity` int NULL DEFAULT 86400 COMMENT '令牌过期秒数 默认一天',
  `refresh_token_validity` int NULL DEFAULT 604800 COMMENT '刷新令牌过期秒数 默认一星期',
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '附加说明',
  `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'false' COMMENT '自动授权',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '认证授权表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_oauth_client_details
-- ----------------------------

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '群id',
  `u_id` bigint NOT NULL COMMENT '发布者id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '公告内容',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements
-- ----------------------------

-- ----------------------------
-- Table structure for announcements_read_records
-- ----------------------------
DROP TABLE IF EXISTS `announcements_read_records`;
CREATE TABLE `announcements_read_records`  (
  `id` bigint NOT NULL,
  `announcements_id` bigint NOT NULL COMMENT '公告id',
  `u_id` bigint NOT NULL COMMENT '阅读人id',
  `is_check` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0：未读 1：已读',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '公告是否已读表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements_read_records
-- ----------------------------

-- ----------------------------
-- Table structure for black
-- ----------------------------
DROP TABLE IF EXISTS `black`;
CREATE TABLE `black`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '拉黑目标类型 1.ip 2uid',
  `target` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '拉黑目标',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_target`(`type` ASC, `target` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '黑名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of black
-- ----------------------------

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型',
  `config_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数键名',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '参数键值',
  `is_del` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, 'system', '{\"title\":\"系统名称\",\"componentType\":\"text\",\"value\":\"Hula-IM\",\"configKey\":\"systemName\",\"type\":\"system\"}', 'systemName', 'HuLa', 0);
INSERT INTO `config` VALUES (2, 'system', '{\"title\":\"系统Logo\",\"componentType\":\"text\",\"value\":\"/static/img/Iogo.png\",\"configKey\":\"logo\",\"type\":\"system\"}', 'logo', '/static/img/Iogo.png', 0);
INSERT INTO `config` VALUES (3, 'qiniu_up_config', '{\"title\":\"空间域名 Domain\",\"componentType\":\"text\",\"value\":\"https://upload-z2.qiniup.com\",\"configKey\":\"qnUploadUrl\",\"type\":\"qiniu_up_config\"}', 'qnUploadUrl', 'https://up-as0.qiniup.com', 0);
INSERT INTO `config` VALUES (4, 'qiniu_up_config', '{\"title\":\"accessKey\",\"componentType\":\"text\",\"value\":\"LXrRo61111111111111AQaUGUJ\",\"configKey\":\"qnAccessKey\",\"type\":\"qiniu_up_config\"}', 'qnAccessKey', 'LXrRo61111111111111AQaUGUJ', 0);
INSERT INTO `config` VALUES (5, 'qiniu_up_config', '{\"title\":\"SecretKey\",\"componentType\":\"text\",\"value\":\"BY22222222LKgX6C300BlVS-llemF2Hg\",\"configKey\":\"qnSecretKey\",\"type\":\"qiniu_up_config\"}', 'qnSecretKey', 'BY22222222LKgX6C300BlVS-llemF2Hg', 0);
INSERT INTO `config` VALUES (6, 'qiniu_up_config', '{\"title\":\"存储空间名称\",\"componentType\":\"text\",\"value\":\"hula\",\"configKey\":\"qnStorageName\",\"type\":\"qiniu_up_config\"}', 'qnStorageName', 'hula-spark', 0);
INSERT INTO `config` VALUES (7, 'qiniu_up_config', '{\"title\":\"七牛云CDN（访问图片用的）\",\"componentType\":\"text\",\"value\":\"https://file.hula.com/\",\"configKey\":\"qnStorageCDN\",\"type\":\"qiniu_up_config\"}', 'qnStorageCDN', 'https://cdn.hulaspark.com', 0);
INSERT INTO `config` VALUES (8, 'system', '{\"title\":\"大群ID\",\"componentType\":\"text\",\"value\":\"1\",\"configKey\":\"roomGroupId\",\"type\":\"system\"}', 'roomGroupId', '1', 0);
INSERT INTO `config` VALUES (9, 'qiniu_up_config', '{\"title\":\"超过多少MB开启分片上传\",\"componentType\":\"text\",\"value\":\"500\",\"configKey\":\"turnSharSize\",\"type\":\"qiniu_up_config\"}', 'turnSharSize', '4', 0);
INSERT INTO `config` VALUES (10, 'qiniu_up_config', '{\"title\":\"分片大小\",\"componentType\":\"text\",\"value\":\"50\",\"configKey\":\"fragmentSize\",\"type\":\"shop_config\"}', 'fragmentSize', '2', 0);
INSERT INTO `config` VALUES (11, 'qiniu_up_config', '{\"title\":\"OSS引擎\",\"componentType\":\"text\",\"value\":\"qiniu\",\"configKey\":\"storageDefault\",\"type\":\"shop_config\"}', 'storageDefault', 'qiniu', 0);
INSERT INTO `config` VALUES (12, 'system', '{\"title\":\"Hula注册邮箱模板\",\"componentType\":\"text\",\"value\":\"\",\"configKey\":\"codeHtmlTemplate\",\"type\":\"shop_config\"}', 'codeHtmlTemplate', '<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\" /><title>{}</title></head><body><div style=\"background-color: #ececec; padding: 15px\"><table cellpadding=\"0\" align=\"center\" style=\"width: 600px; margin: 0px auto; text-align: left; position: relative; border-top-left-radius: 5px; border-top-right-radius: 5px; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px; font-size: 14px; font-family: 微软雅黑, 黑体; line-height: 1.5; box-shadow: rgb(153, 153, 153) 0px 0px 5px; border-collapse: collapse; background-position: initial initial; background-repeat: initial initial; background: #fff\"><tbody><tr><th valign=\"middle\" style=\"height: 25px; line-height: 25px; padding: 15px 35px; border-top-left-radius: 5px; border-top-right-radius: 5px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px; text-align: center\"><img src=\"https://cdn.hulaspark.com/avatar/logo.png\" width=\"180\" height=\"80\" alt=\"HuLa Logo\" /></th></tr><tr><td><div style=\"padding: 6px 35px 10px; background-color: #fff\"><h2 style=\"margin: 5px 0px\"><font color=\"#333333\" style=\"line-height: 20px\"><font style=\"line-height: 22px\" size=\"4\">亲爱的<b>{}</b>用户，您好：</font></font></h2><p>首先感谢您使用{}，请在验证页面输入以下验证码:<br /><p style=\"font-size: 18px; text-align: center; font-weight: bold\">{}</p>本验证码{}分钟内有效，为了保障您的账户安全，请不要告诉别人<br />如果您有什么疑问可以联系管理员，Email: {} </p><p align=\"right\">{}</p><p align=\"right\">{}</p><div style=\"width: 700px; margin: 0 auto\"><div style=\"padding: 10px 10px 0; border-top: 1px solid #ccc; color: #747474; margin-bottom: 20px; line-height: 1.3em; font-size: 12px\"><p>本邮件系统自动发送，请勿回复<br />请保管好您的邮箱，避免账号被他人盗用</p></div></div></div></td></tr></tbody></table></div></body></html>', 0);
INSERT INTO `config` VALUES (13, 'system', '{\"title\":\"Hula管理员邮箱\",\"componentType\":\"text\",\"value\":\"\",\"configKey\":\"masterEmail\",\"type\":\"system\"}', 'masterEmail', '', 0);

-- ----------------------------
-- Table structure for contact
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `mute_notification` tinyint NOT NULL DEFAULT 0 COMMENT '免打扰',
  `shield` tinyint NOT NULL DEFAULT 0 COMMENT '屏蔽会话',
  `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
  `top` tinyint NOT NULL DEFAULT 0 COMMENT '置顶消息',
  `hide` tinyint NOT NULL DEFAULT 0 COMMENT '置顶消息',
  `active_time` datetime(3) NULL DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话最新消息id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uid_room_id`(`uid` ASC, `room_id` ASC) USING BTREE,
  INDEX `idx_room_id_read_time`(`room_id` ASC, `read_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10937855681026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of contact
-- ----------------------------
INSERT INTO `contact` VALUES (10937855681025, 10937855681024, 1, 0, 0, '2025-04-01 19:15:16.811', 1, 0, NULL, NULL, '2025-03-27 04:23:08.420', '2025-04-01 19:15:16.818');

-- ----------------------------
-- Table structure for feed
-- ----------------------------
DROP TABLE IF EXISTS `feed`;
CREATE TABLE `feed`  (
  `id` bigint NOT NULL,
  `u_id` bigint NOT NULL COMMENT '用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '朋友圈文案',
  `permission` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '1' COMMENT 'privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看',
  `media_type` tinyint NULL DEFAULT NULL COMMENT '朋友圈内容类型（0: 纯文字 1: 图片, 2: 视频）',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`u_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feed
-- ----------------------------

-- ----------------------------
-- Table structure for feed_media
-- ----------------------------
DROP TABLE IF EXISTS `feed_media`;
CREATE TABLE `feed_media`  (
  `id` bigint NOT NULL,
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '图片或视频的路径',
  `sort` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feed_media
-- ----------------------------

-- ----------------------------
-- Table structure for feed_target
-- ----------------------------
DROP TABLE IF EXISTS `feed_target`;
CREATE TABLE `feed_target`  (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1 -> 关联标签id 2 -> 关联用户id',
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈可见表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feed_target
-- ----------------------------

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `uid` bigint NOT NULL COMMENT '成员uid',
  `role` int NOT NULL COMMENT '成员角色 1群主 2管理员 3普通成员',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '群备注',
  `de_friend` tinyint NOT NULL DEFAULT 0 COMMENT '屏蔽群 1 -> 屏蔽 0 -> 正常',
  `my_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '我的群昵称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id_role`(`group_id` ASC, `role` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12953449440259 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_member
-- ----------------------------

-- ----------------------------
-- Table structure for item_config
-- ----------------------------
DROP TABLE IF EXISTS `item_config`;
CREATE TABLE `item_config`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `type` int NOT NULL COMMENT '物品类型 1改名卡 2徽章',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
  `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品功能描述',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '功能物品配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of item_config
-- ----------------------------
INSERT INTO `item_config` VALUES (1, 1, NULL, '用户可以使用改名卡，更改自己的名字。HuLa名称全局唯一，快抢订你的专属昵称吧', '2023-03-25 22:27:30.511', '2024-05-11 19:37:03.965');
INSERT INTO `item_config` VALUES (2, 2, 'https://cdn.hulaspark.com/badge/like.png', '爆赞徽章，单条消息被点赞超过10次，即可获得', '2023-05-07 17:50:31.090', '2025-03-26 17:47:00.273');
INSERT INTO `item_config` VALUES (3, 2, 'https://cdn.hulaspark.com/badge/top10.png ', 'HuLa前10名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.100', '2025-03-26 17:47:04.452');
INSERT INTO `item_config` VALUES (4, 2, 'https://cdn.hulaspark.com/badge/top100.png', 'HuLa前100名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.109', '2025-03-26 17:47:07.734');
INSERT INTO `item_config` VALUES (5, 2, 'https://cdn.hulaspark.com/badge/planet.png', 'HuLa知识星球成员的专属徽章', '2023-05-07 17:50:31.109', '2025-03-26 17:47:10.714');
INSERT INTO `item_config` VALUES (6, 2, 'https://cdn.hulaspark.com/badge/active.png', 'HuLa项目贡献者专属徽章', '2023-05-07 17:50:31.109', '2025-03-26 17:47:13.855');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '会话表id',
  `from_uid` bigint NOT NULL COMMENT '消息发送者uid',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `reply_msg_id` bigint NULL DEFAULT NULL COMMENT '回复的消息内容',
  `status` int NOT NULL COMMENT '消息状态 0正常 1删除',
  `gap_count` int NULL DEFAULT NULL COMMENT '与回复的消息间隔多少条',
  `type` int NULL DEFAULT 1 COMMENT '消息类型 1正常文本 2.撤回消息',
  `extra` json NULL COMMENT '扩展信息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_from_uid`(`from_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12971317175297 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for message_mark
-- ----------------------------
DROP TABLE IF EXISTS `message_mark`;
CREATE TABLE `message_mark`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `msg_id` bigint NOT NULL COMMENT '消息表id',
  `uid` bigint NOT NULL COMMENT '标记人uid',
  `type` int NOT NULL COMMENT '标记类型 1点赞 2举报',
  `status` int NOT NULL COMMENT '消息状态 0正常 1取消',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_msg_id`(`msg_id` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_mark
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', '2024-07-10 11:17:15.089', '2024-07-10 11:17:15.089');
INSERT INTO `role` VALUES (2, 'HuLa群聊管理员', '2024-07-10 11:17:15.091', '2024-11-26 12:00:22.452');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '房间类型 1群聊 2单聊',
  `hot_flag` int NULL DEFAULT 0 COMMENT '是否全员展示 0否 1是',
  `active_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '群最后消息的更新时间（热点群不需要写扩散，只更新这里）',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话中的最后一条消息id',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11804382110721 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, 1, '2025-04-01 19:03:23.481', 12971317175296, NULL, '2024-07-10 11:17:15.521', '2025-04-01 19:03:23.563');
INSERT INTO `room` VALUES (11229133317122, 2, 0, '2025-03-29 12:54:31.331', 11791325242368, NULL, '2025-03-27 23:40:34.539', '2025-03-29 12:54:31.449');
INSERT INTO `room` VALUES (11367692149762, 2, 0, '2025-03-28 08:51:18.166', 11367729898496, NULL, '2025-03-28 08:51:09.404', '2025-03-28 08:51:18.214');
INSERT INTO `room` VALUES (11515000300546, 2, 0, '2025-03-28 22:11:20.627', 11569064879104, NULL, '2025-03-28 18:36:30.836', '2025-03-28 22:11:20.686');
INSERT INTO `room` VALUES (11515012883458, 2, 0, '2025-03-28 18:36:33.240', 11515012883460, NULL, '2025-03-28 18:36:33.219', '2025-03-28 18:36:33.291');
INSERT INTO `room` VALUES (11794076705794, 2, 0, '2025-03-29 13:05:27.817', 11794076705796, NULL, '2025-03-29 13:05:27.797', '2025-03-29 13:05:27.873');
INSERT INTO `room` VALUES (11794085094402, 2, 0, '2025-03-29 13:05:29.193', 11794085094404, NULL, '2025-03-29 13:05:29.175', '2025-03-29 13:05:29.234');
INSERT INTO `room` VALUES (11794630353920, 1, 0, '2025-03-29 13:07:39.457', 11794630353927, NULL, '2025-03-29 13:07:39.200', '2025-03-29 13:07:39.546');
INSERT INTO `room` VALUES (11803039933440, 1, 0, '2025-03-29 13:41:04.451', 11803039933446, NULL, '2025-03-29 13:41:04.169', '2025-03-29 13:41:04.542');
INSERT INTO `room` VALUES (11804382110720, 1, 0, '2025-04-01 14:18:46.547', 12899691045888, NULL, '2025-03-29 13:46:24.546', '2025-04-01 14:18:46.615');

-- ----------------------------
-- Table structure for room_friend
-- ----------------------------
DROP TABLE IF EXISTS `room_friend`;
CREATE TABLE `room_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `uid1` bigint NOT NULL COMMENT 'uid1（更小的uid）',
  `uid2` bigint NOT NULL COMMENT 'uid2（更大的uid）',
  `room_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间key由两个uid拼接，先做排序uid1_uid2',
  `de_friend1` int NOT NULL COMMENT '房间状态 0正常 1屏蔽  uid1 屏蔽 uid2',
  `de_friend2` int NOT NULL COMMENT '房间状态 0正常 1屏蔽  uid2 屏蔽 uid1',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_key`(`room_key` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11794085094404 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单聊房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room_friend
-- ----------------------------
INSERT INTO `room_friend` VALUES (11229133317123, 11229133317122, 10937855681024, 11225442329600, '10937855681024,11225442329600', 0, 0, '2025-03-27 23:40:34.548', '2025-03-27 23:40:34.548');
INSERT INTO `room_friend` VALUES (11367692149763, 11367692149762, 11003672699392, 11049889735168, '11003672699392,11049889735168', 0, 0, '2025-03-28 08:51:09.406', '2025-03-28 08:51:09.406');
INSERT INTO `room_friend` VALUES (11515000300547, 11515000300546, 10937855681024, 11419261117440, '10937855681024,11419261117440', 0, 0, '2025-03-28 18:36:30.839', '2025-03-28 18:36:30.839');
INSERT INTO `room_friend` VALUES (11515012883459, 11515012883458, 10937855681024, 11406854366208, '10937855681024,11406854366208', 0, 0, '2025-03-28 18:36:33.221', '2025-03-28 18:36:33.221');
INSERT INTO `room_friend` VALUES (11794076705795, 11794076705794, 10937855681024, 11792579339264, '10937855681024,11792579339264', 0, 0, '2025-03-29 13:05:27.800', '2025-03-29 13:05:27.800');
INSERT INTO `room_friend` VALUES (11794085094403, 11794085094402, 10937855681024, 11793372062720, '10937855681024,11793372062720', 0, 0, '2025-03-29 13:05:29.179', '2025-03-29 13:05:29.179');

-- ----------------------------
-- Table structure for room_group
-- ----------------------------
DROP TABLE IF EXISTS `room_group`;
CREATE TABLE `room_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '群号',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群名称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群头像',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11804382110723 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群聊房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room_group
-- ----------------------------
INSERT INTO `room_group` VALUES (1, 1, 'hula-ds240401', 'HuLa官方频道', 'https://cdn.hulaspark.com/avatar/hula.png', NULL, 0, '2024-07-10 11:17:15.523', '2025-03-31 20:59:45.523');
INSERT INTO `room_group` VALUES (11794630353922, 11794630353920, 'hula-3LeMkyQL', '测试群bug群', 'https://cdn.hulaspark.com/avatar/2439646234/cface6fcc52bae9c3d59bcf8014318b2.webp', NULL, 0, '2025-03-29 13:07:39.211', '2025-03-29 13:07:39.211');
INSERT INTO `room_group` VALUES (11803039933442, 11803039933440, 'hula-3LnXsiDh', '测试群bug2群', 'https://cdn.hulaspark.com/avatar/2439646234/5bc6e2e65225019c6b4a1cf974b2799b.webp', NULL, 0, '2025-03-29 13:41:04.175', '2025-03-29 13:41:04.175');
INSERT INTO `room_group` VALUES (11804382110722, 11804382110720, 'hula-3Lp0iLqL', 'bug反馈群', 'https://cdn.hulaspark.com/avatar/2439646234/cb0d74199060ed6d4f2720b8aa302432.webp', NULL, 0, '2025-03-29 13:46:24.551', '2025-03-29 13:46:24.551');

-- ----------------------------
-- Table structure for secure_invoke_record
-- ----------------------------
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
  `status` tinyint NOT NULL COMMENT '状态 1待执行 2已失败',
  `next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
  `retry_times` int NOT NULL COMMENT '已经重试的次数',
  `max_retry_times` int NOT NULL COMMENT '最大重试次数',
  `fail_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行失败的堆栈',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_next_retry_time`(`next_retry_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of secure_invoke_record
-- ----------------------------

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '敏感词库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sensitive_word
-- ----------------------------
INSERT INTO `sensitive_word` VALUES ('TMD');
INSERT INTO `sensitive_word` VALUES ('tmd');

-- ----------------------------
-- Table structure for target
-- ----------------------------
DROP TABLE IF EXISTS `target`;
CREATE TABLE `target`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `u_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '标签名',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '' COMMENT '标签图标',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天的标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of target
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `sex` int NULL DEFAULT NULL COMMENT '性别 1为男性，2为女性',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `active_status` int NULL DEFAULT 2 COMMENT '在线状态 1在线 2离线',
  `user_state_id` bigint NOT NULL DEFAULT 0 COMMENT '用户状态id',
  `last_opt_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '最后上下线时间',
  `ip_info` json NULL COMMENT 'ip信息',
  `item_id` bigint NULL DEFAULT NULL COMMENT '佩戴的徽章id',
  `status` int NULL DEFAULT 0 COMMENT '使用状态 0.正常 1拉黑',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `avatar_update_time` datetime(3) NULL DEFAULT NULL COMMENT '头像修改时间',
  `num` int NOT NULL DEFAULT 0 COMMENT '调用次数[AI模块]',
  `context` tinyint NOT NULL DEFAULT 0 COMMENT '是否开启上下文[AI模块]',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_active_status_last_opt_time`(`active_status` ASC, `last_opt_time` ASC) USING BTREE,
  INDEX `account_UNIQUE`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12953449440257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (10937855681024, 'Dawn', 'https://cdn.hulaspark.com/avatar/2439646234/ae862ed84ac12da587d79c93ebbe9045.webp', '2439646234@qq.com', '2439646234', NULL, '', 2, 14, '2025-04-01 19:17:56.864', '{\"createIp\": \"206.237.119.215\", \"updateIp\": \"117.181.235.53\", \"createIpDetail\": {\"ip\": \"206.237.119.215\", \"isp\": \"XX\", \"area\": \"\", \"city\": \"XX\", \"isp_id\": \"xx\", \"region\": \"XX\", \"city_id\": \"xx\", \"country\": \"美国\", \"region_id\": \"xx\", \"country_id\": \"US\"}, \"updateIpDetail\": {\"ip\": \"117.181.235.53\", \"isp\": \"移动\", \"area\": \"\", \"city\": \"柳州\", \"isp_id\": \"100025\", \"region\": \"广西\", \"city_id\": \"450200\", \"country\": \"中国\", \"region_id\": \"450000\", \"country_id\": \"CN\"}}', 6, 0, '2025-03-27 04:23:08.393', '2025-04-01 19:17:56.882', 'k.23772439646234', '2025-03-29 13:24:17.085', 0, 0);

-- ----------------------------
-- Table structure for user_apply
-- ----------------------------
DROP TABLE IF EXISTS `user_apply`;
CREATE TABLE `user_apply`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '申请人uid',
  `type` int NOT NULL COMMENT '申请类型 1加好友 2 加群',
  `target_id` bigint NOT NULL COMMENT '接收对象 type: 1 -> uid; type: 2 -> roomGroupId',
  `msg` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请信息',
  `status` int NOT NULL COMMENT '申请状态 1待审批 2同意',
  `read_status` int NOT NULL COMMENT '阅读状态 1未读 2已读',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态 0：未删 1 申请人删除 2 被申请人删除 3都删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_target_id`(`uid` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target_id_read_status`(`target_id` ASC, `read_status` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12835186844673 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_apply
-- ----------------------------

-- ----------------------------
-- Table structure for user_backpack
-- ----------------------------
DROP TABLE IF EXISTS `user_backpack`;
CREATE TABLE `user_backpack`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `item_id` int NOT NULL COMMENT '物品id',
  `status` int NOT NULL COMMENT '使用状态 0.待使用 1已使用',
  `idempotent` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等号',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idempotent`(`idempotent` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12953449440261 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_backpack
-- ----------------------------
INSERT INTO `user_backpack` VALUES (10937855681027, 10937855681024, 1, 0, '1_1_10937855681024', '2025-03-27 04:23:08.580', '2025-03-27 04:23:08.580');
INSERT INTO `user_backpack` VALUES (10937855681028, 10937855681024, 3, 0, '3_1_10937855681024', '2025-03-27 04:23:08.564', '2025-03-27 04:23:08.564');
INSERT INTO `user_backpack` VALUES (10937855681029, 10937855681024, 6, 0, '6_1_10937855681024', '2025-03-27 04:27:34.443', '2025-03-27 04:27:34.443');

-- ----------------------------
-- Table structure for user_block
-- ----------------------------
DROP TABLE IF EXISTS `user_block`;
CREATE TABLE `user_block`  (
  `id` bigint NOT NULL COMMENT '主键',
  `blocker_uid` bigint NOT NULL COMMENT '屏蔽方用户ID',
  `blocked_uid` bigint NOT NULL COMMENT '被屏蔽方用户ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blocker_blocked`(`blocker_uid` ASC, `blocked_uid` ASC) USING BTREE,
  INDEX `idx_blocker`(`blocker_uid` ASC) USING BTREE,
  INDEX `idx_blocked`(`blocked_uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户屏蔽关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_block
-- ----------------------------

-- ----------------------------
-- Table structure for user_emoji
-- ----------------------------
DROP TABLE IF EXISTS `user_emoji`;
CREATE TABLE `user_emoji`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '用户表ID',
  `expression_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情地址',
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDX_USER_EMOJIS_UID`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12943123063809 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表情包' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_emoji
-- ----------------------------
INSERT INTO `user_emoji` VALUES (10951726244352, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/c64611c3bbbcd197552b7f84ac4b709b.jpg', 0, '2025-03-27 05:18:15.512', '2025-03-27 05:18:15.512');
INSERT INTO `user_emoji` VALUES (11099252499456, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/34c18e6c846bf18525a2eb180179ff5a.jpg', 0, '2025-03-27 15:04:28.904', '2025-03-27 15:04:28.904');
INSERT INTO `user_emoji` VALUES (11099458020352, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/bb124e9812456b4f6c9c2b3dcfeb859b.jpg', 0, '2025-03-27 15:05:17.671', '2025-03-27 15:05:17.671');
INSERT INTO `user_emoji` VALUES (12637274416128, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/710683d87faa15a807c58574a6ee8c81.png', 0, '2025-03-31 20:56:01.302', '2025-03-31 20:56:01.302');
INSERT INTO `user_emoji` VALUES (12637333136384, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/322eed8d06539028c2e9c27212ee232c.png', 0, '2025-03-31 20:56:15.350', '2025-03-31 20:56:15.350');
INSERT INTO `user_emoji` VALUES (12637379273728, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/6ac23bb3a9a384ada1e82f41d181e4bc.png', 0, '2025-03-31 20:56:26.793', '2025-03-31 20:56:26.793');
INSERT INTO `user_emoji` VALUES (12637450576896, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/ae4e6c0e10667c33f0066eaf7444c3a9.png', 0, '2025-03-31 20:56:43.103', '2025-03-31 20:56:43.103');

-- ----------------------------
-- Table structure for user_friend
-- ----------------------------
DROP TABLE IF EXISTS `user_friend`;
CREATE TABLE `user_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `friend_uid` bigint NOT NULL COMMENT '好友uid',
  `delete_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `mute_notification` tinyint(1) NOT NULL DEFAULT 0 COMMENT '免打扰',
  `hide_my_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不让他看我（0-允许，1-禁止）',
  `hide_their_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不看他（0-允许，1-禁止）',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_friend_uid`(`uid` ASC, `friend_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11794085094402 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户联系人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_friend
-- ----------------------------
INSERT INTO `user_friend` VALUES (11229133317120, 10937855681024, 11225442329600, 0, 0, 0, 0, NULL, '2025-03-27 23:40:34.527', '2025-03-27 23:40:34.527');
INSERT INTO `user_friend` VALUES (11229133317121, 11225442329600, 10937855681024, 0, 0, 0, 0, NULL, '2025-03-27 23:40:34.529', '2025-03-27 23:40:34.529');
INSERT INTO `user_friend` VALUES (11367692149760, 11003672699392, 11049889735168, 0, 0, 0, 0, NULL, '2025-03-28 08:51:09.397', '2025-03-28 08:51:09.397');
INSERT INTO `user_friend` VALUES (11367692149761, 11049889735168, 11003672699392, 0, 0, 0, 0, NULL, '2025-03-28 08:51:09.399', '2025-03-28 08:51:09.399');
INSERT INTO `user_friend` VALUES (11515000300544, 10937855681024, 11419261117440, 0, 0, 0, 0, NULL, '2025-03-28 18:36:30.828', '2025-03-28 18:36:30.828');
INSERT INTO `user_friend` VALUES (11515000300545, 11419261117440, 10937855681024, 0, 0, 0, 0, NULL, '2025-03-28 18:36:30.830', '2025-03-28 18:36:30.830');
INSERT INTO `user_friend` VALUES (11515012883456, 10937855681024, 11406854366208, 0, 0, 0, 0, NULL, '2025-03-28 18:36:33.214', '2025-03-28 18:36:33.214');
INSERT INTO `user_friend` VALUES (11515012883457, 11406854366208, 10937855681024, 0, 0, 0, 0, NULL, '2025-03-28 18:36:33.215', '2025-03-28 18:36:33.215');
INSERT INTO `user_friend` VALUES (11794076705792, 10937855681024, 11792579339264, 0, 0, 0, 0, NULL, '2025-03-29 13:05:27.789', '2025-03-29 13:05:27.789');
INSERT INTO `user_friend` VALUES (11794076705793, 11792579339264, 10937855681024, 0, 0, 0, 0, NULL, '2025-03-29 13:05:27.791', '2025-03-29 13:05:27.791');
INSERT INTO `user_friend` VALUES (11794085094400, 10937855681024, 11793372062720, 0, 0, 0, 0, NULL, '2025-03-29 13:05:29.167', '2025-03-29 13:05:29.167');
INSERT INTO `user_friend` VALUES (11794085094401, 11793372062720, 10937855681024, 0, 0, 0, 0, NULL, '2025-03-29 13:05:29.169', '2025-03-29 13:05:29.169');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------

-- ----------------------------
-- Table structure for user_state
-- ----------------------------
DROP TABLE IF EXISTS `user_state`;
CREATE TABLE `user_state`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态名',
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态图标',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天用户状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_state
-- ----------------------------
INSERT INTO `user_state` VALUES (1, '在线', '/status/online.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (2, '离开', '/status/leave.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (3, '忙碌', '/status/busy.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (4, '请勿打扰', '/status/IonBan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (5, '隐身', '/status/cloaking.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (6, '离线', '/status/offline.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (7, '今日天气', '/status/weather_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (8, '一言难尽', '/status/hardtosay@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (9, '我太难了', '/status/toohard@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (10, '难得糊涂', '/status/nandehutu.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (11, '元气满满', '/status/fullofyuanqi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (12, '嗨到飞起', '/status/happytofly@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (13, '水逆退散', '/status/luck@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (14, '好运锦鲤', '/status/jinli@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (15, '恋爱中', '/status/relationship_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (16, '我crush了', '/status/crush.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (17, '被掏空', '/status/tkong.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (18, '听歌中', '/status/music@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (19, '我没事', '/status/imfine_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (20, '学习中', '/status/study_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (21, '睡觉中', '/status/sleeping_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (22, '搬砖中', '/status/banzhuan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (23, '想静静', '/status/bequiet@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (24, '运动中', '/status/yundongzhong@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (25, '我想开了', '/status/woxiangkaile.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (26, '信号弱', '/status/signal_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (27, '追剧中', '/status/tv_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (28, '美滋滋', '/status/meizizi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (29, '摸鱼中', '/status/fish@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (30, '无聊中', '/status/boring@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (31, '悠哉哉', '/status/youzaizai@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (32, '去旅行', '/status/gototravel.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (33, '游戏中', '/status/game_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);

-- ----------------------------
-- Table structure for user_target_rel
-- ----------------------------
DROP TABLE IF EXISTS `user_target_rel`;
CREATE TABLE `user_target_rel`  (
  `id` bigint NOT NULL,
  `u_id` bigint NOT NULL COMMENT '人员id',
  `friend_id` bigint NOT NULL COMMENT '被绑定的人员id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC, `friend_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '人员标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_target_rel
-- ----------------------------

-- ----------------------------
-- Table structure for worker_node
-- ----------------------------
DROP TABLE IF EXISTS `worker_node`;
CREATE TABLE `worker_node`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'auto;increment id',
  `host_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主机名',
  `port` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '端口',
  `type` int NOT NULL COMMENT '节点类型:;ACTUAL 或者 CONTAINER',
  `launch_date` date NOT NULL COMMENT '上线日期',
  `modified` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DB;WorkerID Assigner for UID Generator' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of worker_node
-- ----------------------------
INSERT INTO `worker_node` VALUES (80, '172.19.0.2', '1743020152589-45802', 2, '2025-03-27', '2025-03-27 04:15:53', '2025-03-27 04:15:53');
INSERT INTO `worker_node` VALUES (81, '172.24.0.5', '1743020334526-47834', 2, '2025-03-27', '2025-03-27 04:18:55', '2025-03-27 04:18:55');
INSERT INTO `worker_node` VALUES (82, '172.19.0.2', '1743057680832-8203', 2, '2025-03-27', '2025-03-27 14:41:21', '2025-03-27 14:41:21');

-- ----------------------------
-- Table structure for wx_msg
-- ----------------------------
DROP TABLE IF EXISTS `wx_msg`;
CREATE TABLE `wx_msg`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `open_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid用户标识',
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户消息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_open_id`(`open_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wx_msg
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
