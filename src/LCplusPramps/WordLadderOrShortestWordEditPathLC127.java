package LCplusPramps;

import java.util.*;

public class WordLadderOrShortestWordEditPathLC127 {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (beginWord.length() != endWord.length()) return 0;
        Set<String> wordSet = new HashSet<String>(wordList);
        if (!wordSet.contains(endWord)) return 0;
        Queue<String> queueNode = new LinkedList<String>();
        queueNode.add(beginWord);
        Set<String> visited = new HashSet<String>();
        visited.add(beginWord);
        int transformations=1;
        while(!queueNode.isEmpty()){
            int size=queueNode.size();
            for(int i=0;i<size;i++){
                char[] wordCharArr = queueNode.poll().toCharArray();
                for(int j=0;j<wordCharArr.length;j++){
                    char originalChar = wordCharArr[j];
                    for (char k = 'a'; k <= 'z'; k++){
                        if(k==originalChar) continue;
                        wordCharArr[j]= k;
                        String transformedWord = String.valueOf(wordCharArr);
                        if(wordSet.contains(transformedWord)){
                            if(endWord.equals(transformedWord)){
                                return transformations+1;
                            }
                            queueNode.add(transformedWord);
                            wordSet.remove(transformedWord);
                        }
                    }
                    wordCharArr[j] = originalChar;
                }
            }
        transformations++;
        }
        return 0;
    }

    public static void main(String[] args) {
        String source = "bit", target = "dog";
        String[] arr = {"but","put","big","pot","pog","dog","lot"};
        List<String> wordlst= Arrays.asList(arr);
        int length = new WordLadderOrShortestWordEditPathLC127().ladderLength(source,target,wordlst);
        System.out.println(length);
        String source1 = "hit", target1 = "cog";
        String[] arr1 = {"hot","dot","dog","lot","log","cog"};
        List<String> wordlst1= Arrays.asList(arr1);
        int length1 = new WordLadderOrShortestWordEditPathLC127().ladderLength(source1,target1,wordlst1);
        System.out.println(length1);
        String source2 ="ymain", target2 ="oecij";
        String[] arr2 ={"ymann","yycrj","oecij","ymcnj","yzcrj","yycij","xecij","yecij","ymanj","yzcnj","ymain"};
        List<String> wordlst2= Arrays.asList(arr2);
        int length2 = new WordLadderOrShortestWordEditPathLC127().ladderLength(source2,target2,wordlst2);
        System.out.println(length2);
    }
}
