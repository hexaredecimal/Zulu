" Vim syntax file
" Language:     Zulu
" Filenames:    *.zulu


" quit when a syntax file was already loaded
if exists("b:current_syntax")
  finish
endif

" Disable spell checking of syntax.
syn spell notoplevel

" zulu is case sensitive.

" lowercase identifier - the standard way to match
" syn match    zuluLCIdentifier /\<\(\l\|_\)\(\w\|'\)*\>/

syn match    zuluKeyChar    "|"

" Some convenient clusters
syn cluster  zuluAllErrs contains=zuluBraceErr,zuluBrackErr,zuluParenErr,zuluCommentErr,zuluEndErr,zuluThenErr

syn cluster  zuluAENoParen contains=zuluBraceErr,zuluBrackErr,zuluCommentErr,zuluEndErr,zuluThenErr

syn cluster  zuluContained contains=zuluTodo,zuluPreDef,zuluModParam,zuluModParam1,zuluPreMPRestr,zuluMPRestr,zuluMPRestr1,zuluMPRestr2,zuluMPRestr3,zuluModRHS,zuluFuncWith,zuluFuncStruct,zuluModTypeRestr,zuluModTRWith,zuluWith,zuluWithRest,zuluModType,zuluFullMod


" Enclosing delimiters
syn region   zuluEncl transparent matchgroup=zuluKeyword start="(" matchgroup=zuluKeyword end=")" contains=ALLBUT,@zuluContained,zuluParenErr
syn region   zuluEncl transparent matchgroup=zuluKeyword start="{" matchgroup=zuluKeyword end="}"  contains=ALLBUT,@zuluContained,zuluBraceErr
syn region   zuluEncl transparent matchgroup=zuluKeyword start="\[" matchgroup=zuluKeyword end="\]" contains=ALLBUT,@zuluContained,zuluBrackErr
syn region   zuluEncl transparent matchgroup=zuluKeyword start="#\[" matchgroup=zuluKeyword end="\]" contains=ALLBUT,@zuluContained,zuluBrackErr


" Comments
syn region zuluComment start="%%" end="$" contains=zuluComment,zuluTodo,@Spell
" syn region   zuluComment start="\%%" contains=zuluComment,zuluTodo,@Spell
syn keyword  zuluTodo contained TODO FIXME

syn keyword  zuluKeyword  module doc when receive case of end and or global not def opt defguard import
syn keyword  zuluKeyword  unitbase strict pack unpack ast extern binary_operation unary_operation inline type as

syn keyword  zuluType     Number String List XML Boolean Reference Pointer Atom Null

syn keyword  zuluThread       Rest Message
syn keyword  zuluBoolean      true false
syn match    zuluConstructor  "(\s*)"
syn match    zuluConstructor  "\[\s*\]"
syn match    zuluConstructor  "#\[\s*\]"
syn match    zuluConstructor  "\u\(\w\|'\)*\>"

syn match zuluFnIdent "[a-zA-Z_][a-zA-Z0-9_]*\s*\ze("

" Module prefix
syn match    zuluModPath      "\u\(\w\|'\)*\."he=e-1

syn match    zuluCharacter    +#"\\""\|#"."\|#"\\\d\d\d"+
syn match    zuluCharErr      +#"\\\d\d"\|#"\\\d"+
syn region   zuluString       start=+"+ skip=+\\\\\|\\"+ end=+"+ contains=@Spell

syn match    zuluFunDef       "=>"
syn match    zuluOperator     "::"
syn match    zuluAnyVar       "\<_\>"
syn match    zuluKeyChar      "!"
syn match    zuluKeyChar      ";"
syn match    zuluKeyChar      "\*"
syn match    zuluKeyChar      "="

syn match    zuluNumber        "\<-\=\d\+\>"
syn match    zuluNumber        "\<-\=0[x|X]\x\+\>"
syn match    zuluReal          "\<-\=\d\+\.\d*\([eE][-+]\=\d\+\)\=[fl]\=\>"

" Synchronization
syn sync minlines=20
syn sync maxlines=500

hi def link zuluComment      Comment

hi def link zuluModPath      Include
hi def link zuluModule       Include
hi def link zuluModParam1    Include
hi def link zuluModType      Include
hi def link zuluMPRestr3     Include
hi def link zuluFullMod      Include
hi def link zuluModTypeRestr Include
hi def link zuluWith         Include
hi def link zuluMTDef        Include

hi def link zuluConstructor  Constant

hi def link zuluModPreRHS    Keyword
hi def link zuluMPRestr2     Keyword
hi def link zuluKeyword      Keyword
hi def link zuluFunDef       Keyword
hi def link zuluRefAssign    Keyword
hi def link zuluKeyChar      Keyword
hi def link zuluAnyVar       Keyword
hi def link zuluTopStop      Keyword
hi def link zuluOperator     Keyword
hi def link zuluThread       Keyword

hi def link zuluBoolean      Boolean
hi def link zuluCharacter    Character
hi def link zuluNumber       Number
hi def link zuluReal         Float
hi def link zuluString       String
hi def link zuluType         Type
hi def link zuluTodo         Todo
hi def link zuluEncl         Keyword
hi def link zuluFnIdent      Function

let b:current_syntax = "zulu"

" vim: ts=8
