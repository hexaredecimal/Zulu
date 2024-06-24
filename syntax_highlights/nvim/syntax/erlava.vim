" Vim syntax file
" Language:     erlava
" Filenames:    *.evl


" quit when a syntax file was already loaded
if exists("b:current_syntax")
  finish
endif

" Disable spell checking of syntax.
syn spell notoplevel

" erlava is case sensitive.

" lowercase identifier - the standard way to match
" syn match    erlavaLCIdentifier /\<\(\l\|_\)\(\w\|'\)*\>/

syn match    erlavaKeyChar    "|"

" Some convenient clusters
syn cluster  erlavaAllErrs contains=smlBraceErr,smlBrackErr,smlParenErr,smlCommentErr,smlEndErr,smlThenErr

syn cluster  erlavaAENoParen contains=smlBraceErr,smlBrackErr,smlCommentErr,smlEndErr,smlThenErr

syn cluster  erlavaContained contains=smlTodo,smlPreDef,smlModParam,smlModParam1,smlPreMPRestr,smlMPRestr,smlMPRestr1,smlMPRestr2,smlMPRestr3,smlModRHS,smlFuncWith,smlFuncStruct,smlModTypeRestr,smlModTRWith,smlWith,smlWithRest,smlModType,smlFullMod


" Enclosing delimiters
syn region   erlavaEncl transparent matchgroup=smlKeyword start="(" matchgroup=smlKeyword end=")" contains=ALLBUT,@smlContained,smlParenErr
syn region   erlavaEncl transparent matchgroup=smlKeyword start="{" matchgroup=smlKeyword end="}"  contains=ALLBUT,@smlContained,smlBraceErr
syn region   erlavaEncl transparent matchgroup=smlKeyword start="\[" matchgroup=smlKeyword end="\]" contains=ALLBUT,@smlContained,smlBrackErr
syn region   erlavaEncl transparent matchgroup=smlKeyword start="#\[" matchgroup=smlKeyword end="\]" contains=ALLBUT,@smlContained,smlBrackErr


" Comments
syn region erlavaComment start="%%" end="$" contains=smlComment,smlTodo,@Spell
" syn region   erlavaComment start="\%%" contains=smlComment,smlTodo,@Spell
syn keyword  erlavaTodo contained TODO FIXME XXX

syn keyword  erlavaKeyword  module doc when receive case of end and or global not def opt defguard import
syn keyword  erlavaKeyword  unitbase strict pack unpack ast extern binary_operation unary_operation inline type 

syn keyword  erlavaType     Number String List XML Boolean Reference

syn keyword  erlavaThread       Rest Message
syn keyword  erlavaBoolean      true false
syn match    erlavaConstructor  "(\s*)"
syn match    erlavaConstructor  "\[\s*\]"
syn match    erlavaConstructor  "#\[\s*\]"
syn match    erlavaConstructor  "\u\(\w\|'\)*\>"

syn match erlavaFnIdent "[a-zA-Z_][a-zA-Z0-9_]*\s*\ze("

" Module prefix
syn match    erlavaModPath      "\u\(\w\|'\)*\."he=e-1

syn match    erlavaCharacter    +#"\\""\|#"."\|#"\\\d\d\d"+
syn match    erlavaCharErr      +#"\\\d\d"\|#"\\\d"+
syn region   erlavaString       start=+"+ skip=+\\\\\|\\"+ end=+"+ contains=@Spell

syn match    erlavaFunDef       "=>"
syn match    erlavaOperator     "::"
syn match    erlavaAnyVar       "\<_\>"
syn match    erlavaKeyChar      "!"
syn match    erlavaKeyChar      ";"
syn match    erlavaKeyChar      "\*"
syn match    erlavaKeyChar      "="

syn match    erlavaNumber        "\<-\=\d\+\>"
syn match    erlavaNumber        "\<-\=0[x|X]\x\+\>"
syn match    erlavaReal          "\<-\=\d\+\.\d*\([eE][-+]\=\d\+\)\=[fl]\=\>"

" Synchronization
syn sync minlines=20
syn sync maxlines=500

hi def link erlavaComment      Comment

hi def link erlavaModPath      Include
hi def link erlavaModule       Include
hi def link erlavaModParam1    Include
hi def link erlavaModType      Include
hi def link erlavaMPRestr3     Include
hi def link erlavaFullMod      Include
hi def link erlavaModTypeRestr Include
hi def link erlavaWith         Include
hi def link erlavaMTDef        Include

hi def link erlavaConstructor  Constant

hi def link erlavaModPreRHS    Keyword
hi def link erlavaMPRestr2     Keyword
hi def link erlavaKeyword      Keyword
hi def link erlavaFunDef       Keyword
hi def link erlavaRefAssign    Keyword
hi def link erlavaKeyChar      Keyword
hi def link erlavaAnyVar       Keyword
hi def link erlavaTopStop      Keyword
hi def link erlavaOperator     Keyword
hi def link erlavaThread       Keyword

hi def link erlavaBoolean      Boolean
hi def link erlavaCharacter    Character
hi def link erlavaNumber       Number
hi def link erlavaReal         Float
hi def link erlavaString       String
hi def link erlavaType         Type
hi def link erlavaTodo         Todo
hi def link erlavaEncl         Keyword
hi def link erlavaFnIdent      Function

let b:current_syntax = "erlava"

" vim: ts=8
